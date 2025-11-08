package com.disasterrelief.relief_system.service;

import com.disasterrelief.relief_system.model.Resource;
import com.disasterrelief.relief_system.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<Resource> getAvailableResources() {
        return resourceRepository.findAvailableResources();
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public List<Resource> getResourcesByCategory(Resource.ResourceCategory category) {
        return resourceRepository.findByCategory(category);
    }

    @Transactional
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource updateResource(Long id, Resource updatedResource) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setName(updatedResource.getName());
        resource.setDescription(updatedResource.getDescription());
        resource.setCategory(updatedResource.getCategory());
        resource.setQuantity(updatedResource.getQuantity());
        resource.setLocation(updatedResource.getLocation());
        resource.setSource(updatedResource.getSource());

        return resourceRepository.save(resource);
    }

    @Transactional
    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }

    @Transactional
    public boolean allocateResource(Long id, Integer quantity) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (resource.getQuantity() < quantity) {
            return false;
        }

        resource.setQuantity(resource.getQuantity() - quantity);
        resourceRepository.save(resource);
        return true;
    }

    @Transactional
    public void returnResource(Long id, Integer quantity) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setQuantity(resource.getQuantity() + quantity);
        resourceRepository.save(resource);
    }
}
