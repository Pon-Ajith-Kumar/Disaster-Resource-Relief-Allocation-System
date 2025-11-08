package com.disasterrelief.relief_system.repository;

import com.disasterrelief.relief_system.model.AllocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AllocationLogRepository extends JpaRepository<AllocationLog, Long> {
    List<AllocationLog> findByRequestId(Long requestId);

    @Query("SELECT a FROM AllocationLog a WHERE a.allocatedTime BETWEEN ?1 AND ?2")
    List<AllocationLog> findByAllocatedTimeBetween(LocalDateTime start, LocalDateTime end);

    // Derived query to count allocations between two timestamps
    Long countByAllocatedTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(a) FROM AllocationLog a WHERE a.allocatedTime >= ?1")
    Long countAllocationsAfter(LocalDateTime date);
}

