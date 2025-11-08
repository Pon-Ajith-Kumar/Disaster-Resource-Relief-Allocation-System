package com.disasterrelief.relief_system.repository;

import com.disasterrelief.relief_system.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByCategory(Resource.ResourceCategory category);
    List<Resource> findByLocation(String location);
    List<Resource> findByQuantityGreaterThan(Integer quantity);

    @Query("SELECT r FROM Resource r WHERE r.quantity > 0")
    List<Resource> findAvailableResources();

    @Query("SELECT r FROM Resource r WHERE r.name LIKE %?1% AND r.quantity >= ?2")
    List<Resource> findByNameAndQuantity(String name, Integer quantity);
}