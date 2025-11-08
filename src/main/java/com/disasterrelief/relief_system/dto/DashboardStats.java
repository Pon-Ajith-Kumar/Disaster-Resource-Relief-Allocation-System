package com.disasterrelief.relief_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalRequests;
    private Long pendingRequests;
    private Long allocatedRequests;
    private Long rejectedRequests;
    private Long totalResources;
    private Long availableResources;
    private Long totalUsers;
    private Long ngoCount;
    private Long citizenCount;
    private Long todayAllocations;
}