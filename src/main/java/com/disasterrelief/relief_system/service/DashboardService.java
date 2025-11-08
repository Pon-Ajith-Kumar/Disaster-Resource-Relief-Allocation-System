package com.disasterrelief.relief_system.service;

import com.disasterrelief.relief_system.dto.DashboardStats;
import com.disasterrelief.relief_system.model.Request;
import com.disasterrelief.relief_system.model.User;
import com.disasterrelief.relief_system.repository.AllocationLogRepository;
import com.disasterrelief.relief_system.repository.RequestRepository;
import com.disasterrelief.relief_system.repository.ResourceRepository;
import com.disasterrelief.relief_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ResourceRepository resourceRepository;
    private final AllocationLogRepository allocationLogRepository;

    public DashboardStats getDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        // Safe counting with null checks and defaults
        Long totalRequests = Optional.ofNullable(requestRepository.count()).orElse(0L);
        Long pendingRequests = Optional.ofNullable(requestRepository.countByStatus(Request.Status.PENDING)).orElse(0L);
        Long allocatedRequests = Optional.ofNullable(requestRepository.countByStatus(Request.Status.ALLOCATED)).orElse(0L);
        Long rejectedRequests = Optional.ofNullable(requestRepository.countByStatus(Request.Status.REJECTED)).orElse(0L);
        Long totalResources = Optional.ofNullable(resourceRepository.count()).orElse(0L);
        Long availableResources = Long.valueOf(Optional.ofNullable(resourceRepository.findAvailableResources()).orElse(Collections.emptyList()).size());
        Long todayAllocations = Optional.ofNullable(allocationLogRepository.countByAllocatedTimeBetween(startOfDay, endOfDay)).orElse(0L);
        Long totalUsers = Optional.ofNullable(userRepository.count()).orElse(0L);
        Long ngoCount = Long.valueOf(Optional.ofNullable(userRepository.findByRole(User.UserRole.NGO)).orElse(Collections.emptyList()).size());
        Long citizenCount = Long.valueOf(Optional.ofNullable(userRepository.findByRole(User.UserRole.CITIZEN)).orElse(Collections.emptyList()).size());

        return new DashboardStats(
            totalRequests,
            pendingRequests,
            allocatedRequests,
            rejectedRequests,
            totalResources,
            availableResources,
            totalUsers,
            ngoCount,
            citizenCount,
            todayAllocations
        );
    }
}