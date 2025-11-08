package com.disasterrelief.relief_system.controller;

import com.disasterrelief.relief_system.dto.ApiResponse;
import com.disasterrelief.relief_system.model.AllocationLog;
import com.disasterrelief.relief_system.model.User;
import com.disasterrelief.relief_system.service.AllocationService;
import com.disasterrelief.relief_system.service.DashboardService;
import com.disasterrelief.relief_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final DashboardService dashboardService;
    private final AllocationService allocationService;
    private final UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")  // Allow any authenticated user
    public ResponseEntity<?> getDashboardStats() {
        return ResponseEntity.ok(new ApiResponse(true, "Dashboard stats retrieved", dashboardService.getDashboardStats()));
    }

    @PostMapping("/allocate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> manualAllocate(
            @RequestParam Long requestId,
            @RequestParam Long resourceId,
            @RequestParam(required = false) String notes) {
        try {
            AllocationLog allocation = allocationService.manualAllocate(requestId, resourceId, notes);
            return ResponseEntity.ok(new ApiResponse(true, "Manual allocation successful", allocation));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Allocation failed: " + e.getMessage()));
        }
    }

    @GetMapping("/allocations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAllocations() {
        var allocations = allocationService.getAllAllocations();
        return ResponseEntity.ok(new ApiResponse(true, "Allocations retrieved", allocations));
    }

    @GetMapping("/allocations/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllocationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        var allocations = allocationService.getAllocationsByDateRange(start, end);
        return ResponseEntity.ok(new ApiResponse(true, "Allocations within range retrieved", allocations));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse(true, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error retrieving users: " + e.getMessage()));
        }
    }
}
