package com.disasterrelief.relief_system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AllocationDetails {
    private Long id;
    private Long requestId;
    private Long resourceId;
    private String resourceName;
    private String requesterName;
    private Integer quantity;
    private String location;
    private String status;
    private String notes;
    private LocalDateTime allocatedTime;
}