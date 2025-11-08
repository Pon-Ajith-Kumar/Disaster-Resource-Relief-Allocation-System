package com.disasterrelief.relief_system.service;

import com.disasterrelief.relief_system.model.AllocationLog;
import com.disasterrelief.relief_system.model.Request;
import com.disasterrelief.relief_system.model.Resource;
import com.disasterrelief.relief_system.dto.AllocationDetails;
import com.disasterrelief.relief_system.repository.AllocationLogRepository;
import com.disasterrelief.relief_system.repository.RequestRepository;
import com.disasterrelief.relief_system.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllocationService {

    private final RequestRepository requestRepository;
    private final ResourceRepository resourceRepository;
    private final AllocationLogRepository allocationLogRepository;

    // Breaking circular dependency by using method injection
    @Autowired
    private ResourceService resourceService;

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void autoAllocateResources() {
        log.info("Starting auto allocation process at {}", LocalDateTime.now());

        try {
            // Get all pending requests ordered by priority and creation time
            List<Request> pendingRequests = requestRepository.findPendingOrderedByPriority();
            if (pendingRequests.isEmpty()) {
                log.debug("No pending requests found for allocation");
                return;
            }

            for (Request request : pendingRequests) {
                try {
                    // Validate request
                    if (request.getQuantity() == null || request.getQuantity() <= 0) {
                        log.error("Invalid quantity for request {}: {}", request.getId(), request.getQuantity());
                        continue;
                    }

                    // Find matching resources ordered by available quantity
                    List<Resource> matchingResources = resourceRepository
                        .findByNameAndQuantity(request.getResourceName(), request.getQuantity())
                        .stream()
                        .filter(r -> r.getQuantity() >= request.getQuantity())
                        .sorted((r1, r2) -> r2.getQuantity().compareTo(r1.getQuantity()))
                        .toList();

                    if (matchingResources.isEmpty()) {
                        log.debug("No matching resources found for request {}", request.getId());
                        continue;
                    }

                    // Get the resource with most availability
                    Resource resource = matchingResources.get(0);

                    // Try to allocate with synchronization to prevent race conditions
                    synchronized (this) {
                        if (resourceService.allocateResource(resource.getId(), request.getQuantity())) {
                            // Update request status
                            request.setStatus(Request.Status.ALLOCATED);
                            Request savedRequest = requestRepository.save(request);

                            // Log allocation
                            AllocationLog allocationLog = new AllocationLog();
                            allocationLog.setRequestId(savedRequest.getId());
                            allocationLog.setResourceId(resource.getId());
                            allocationLog.setQuantity(request.getQuantity());
                            allocationLog.setNotes("Auto-allocated by system");
                            allocationLogRepository.save(allocationLog);

                            log.info("Successfully allocated request {} to resource {}", 
                                savedRequest.getId(), resource.getId());
                        } else {
                            log.warn("Failed to allocate resource {} for request {}", 
                                resource.getId(), request.getId());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing request {}: {}", request.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error in auto allocation process: {}", e.getMessage(), e);
        } finally {
            log.info("Completed auto allocation process at {}", LocalDateTime.now());
        }
    }

    @Transactional
    public AllocationLog manualAllocate(Long requestId, Long resourceId, String notes) {
        // Input validation
        if (requestId == null || resourceId == null) {
            throw new IllegalArgumentException("Request ID and Resource ID are required");
        }

        // Fetch request and resource with validation
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found: " + resourceId));

        // Validate request state
        if (request.getStatus() != Request.Status.PENDING) {
            throw new RuntimeException("Request " + requestId + " is not in pending state. Current status: " + request.getStatus());
        }

        // Validate quantities
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("Invalid request quantity: " + request.getQuantity());
        }
        if (resource.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient resource quantity. Available: " + 
                resource.getQuantity() + ", Requested: " + request.getQuantity());
        }

        // Synchronized block to prevent race conditions
        synchronized (this) {
            if (!resourceService.allocateResource(resourceId, request.getQuantity())) {
                throw new RuntimeException("Failed to allocate resource. Please try again.");
            }

            // Update request status
            request.setStatus(Request.Status.ALLOCATED);
            Request savedRequest = requestRepository.save(request);

            // Create allocation log
            AllocationLog allocationLog = new AllocationLog();
            allocationLog.setRequestId(requestId);
            allocationLog.setResourceId(resourceId);
            allocationLog.setQuantity(request.getQuantity());
            allocationLog.setNotes(notes != null ? notes : "Manually allocated");

            // Save and return allocation log
            try {
                return allocationLogRepository.save(allocationLog);
            } catch (Exception e) {
                // Rollback resource allocation if logging fails
                resourceService.returnResource(resourceId, request.getQuantity());
                request.setStatus(Request.Status.PENDING);
                requestRepository.save(request);
                throw new RuntimeException("Failed to save allocation log: " + e.getMessage());
            }
        }
    }

    public List<AllocationDetails> getAllAllocations() {
        return allocationLogRepository.findAll().stream()
            .map(this::convertToAllocationDetails)
            .toList();
    }

    public List<AllocationDetails> getAllocationsByDateRange(LocalDateTime start, LocalDateTime end) {
        return allocationLogRepository.findByAllocatedTimeBetween(start, end).stream()
            .map(this::convertToAllocationDetails)
            .toList();
    }

    private AllocationDetails convertToAllocationDetails(AllocationLog log) {
        AllocationDetails details = new AllocationDetails();
        details.setId(log.getId());
        details.setRequestId(log.getRequestId());
        details.setResourceId(log.getResourceId());
        details.setQuantity(log.getQuantity());
        details.setNotes(log.getNotes());
        details.setAllocatedTime(log.getAllocatedTime());

        // Fetch additional details
        try {
            Request request = requestRepository.findById(log.getRequestId()).orElse(null);
            if (request != null) {
                details.setLocation(request.getLocation());
                details.setStatus(request.getStatus().toString());
                details.setResourceName(request.getResourceName());
            }
        } catch (Exception e) {
            details.setNotes(details.getNotes() + " (Request details unavailable)");
        }

        return details;
    }
}
