package com.disasterrelief.relief_system.service;

import com.disasterrelief.relief_system.model.Request;
import com.disasterrelief.relief_system.model.Resource;
import com.disasterrelief.relief_system.repository.RequestRepository;
import com.disasterrelief.relief_system.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceService resourceService;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public List<Request> getRequestsByUserId(Long userId) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Request> getRequestsByStatus(Request.Status status) {
        return requestRepository.findByStatus(status);
    }

    public List<Request> getPendingRequestsByPriority() {
        return requestRepository.findPendingOrderedByPriority();
    }

    @Transactional
    public Request createRequest(Request request) {
        // Validate resource exists and has enough quantity
        Resource resource = resourceRepository.findByNameAndQuantity(request.getResourceName(), request.getQuantity())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Resource not available in requested quantity"));

        request.setResourceId(resource.getId());
        request.setStatus(Request.Status.PENDING);
        
        return requestRepository.save(request);
    }

    @Transactional
    public Request updateRequestStatus(Long id, Request.Status newStatus) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // If rejecting or completing a previously allocated request, return the resources
        if ((newStatus == Request.Status.REJECTED || newStatus == Request.Status.COMPLETED) 
            && request.getStatus() == Request.Status.ALLOCATED) {
            resourceService.returnResource(request.getResourceId(), request.getQuantity());
        }
        // If allocating, try to allocate resources
        else if (newStatus == Request.Status.ALLOCATED && request.getStatus() == Request.Status.PENDING) {
            boolean allocated = resourceService.allocateResource(request.getResourceId(), request.getQuantity());
            if (!allocated) {
                throw new RuntimeException("Resource allocation failed - insufficient quantity");
            }
        }

        request.setStatus(newStatus);
        return requestRepository.save(request);
    }

    @Transactional
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
                
        // If the request was allocated, return the resources
        if (request.getStatus() == Request.Status.ALLOCATED) {
            resourceService.returnResource(request.getResourceId(), request.getQuantity());
        }
        
        requestRepository.deleteById(id);
    }
}
