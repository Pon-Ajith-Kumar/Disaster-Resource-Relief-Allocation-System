package com.disasterrelief.relief_system.controller;

import com.disasterrelief.relief_system.dto.ApiResponse;
import com.disasterrelief.relief_system.model.Resource;
import com.disasterrelief.relief_system.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<?> getAllResources() {
        return ResponseEntity.ok(new ApiResponse(true, "Resources retrieved", resourceService.getAllResources()));
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableResources() {
        return ResponseEntity.ok(new ApiResponse(true, "Available resources retrieved", 
            resourceService.getAvailableResources()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .map(resource -> ResponseEntity.ok(new ApiResponse(true, "Resource found", resource)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getResourcesByCategory(@PathVariable Resource.ResourceCategory category) {
        return ResponseEntity.ok(new ApiResponse(true, "Resources by category retrieved", 
            resourceService.getResourcesByCategory(category)));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<?> createResource(@Valid @RequestBody Resource resource) {
        try {
            Resource savedResource = resourceService.createResource(resource);
            return ResponseEntity.ok(new ApiResponse(true, "Resource created successfully", savedResource));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to create resource: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @Valid @RequestBody Resource resource) {
        try {
            Resource updatedResource = resourceService.updateResource(id, resource);
            return ResponseEntity.ok(new ApiResponse(true, "Resource updated successfully", updatedResource));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to update resource: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NGO')")
    public ResponseEntity<?> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteResource(id);
            return ResponseEntity.ok(new ApiResponse(true, "Resource deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to delete resource: " + e.getMessage()));
        }
    }
}
