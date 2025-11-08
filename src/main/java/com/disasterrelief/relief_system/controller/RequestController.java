package com.disasterrelief.relief_system.controller;

import com.disasterrelief.relief_system.dto.ApiResponse;
import com.disasterrelief.relief_system.model.Request;
import com.disasterrelief.relief_system.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(new ApiResponse(true, "Requests retrieved", requestService.getAllRequests()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(request -> ResponseEntity.ok(new ApiResponse(true, "Request found", request)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> getRequestsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse(true, "User requests retrieved", 
            requestService.getRequestsByUserId(userId)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<?> getRequestsByStatus(@PathVariable Request.Status status) {
        return ResponseEntity.ok(new ApiResponse(true, "Requests by status retrieved", 
            requestService.getRequestsByStatus(status)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<?> getPendingRequestsByPriority() {
        return ResponseEntity.ok(new ApiResponse(true, "Pending requests retrieved", 
            requestService.getPendingRequestsByPriority()));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('CITIZEN', 'NGO')")
    public ResponseEntity<?> createRequest(@Valid @RequestBody Request request) {
        try {
            Request savedRequest = requestService.createRequest(request);
            return ResponseEntity.ok(new ApiResponse(true, "Request created successfully", savedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to create request: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestParam Request.Status status) {
        try {
            Request updatedRequest = requestService.updateRequestStatus(id, status);
            return ResponseEntity.ok(new ApiResponse(true, "Request status updated successfully", updatedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to update request status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        try {
            requestService.deleteRequest(id);
            return ResponseEntity.ok(new ApiResponse(true, "Request deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to delete request: " + e.getMessage()));
        }
    }
}
