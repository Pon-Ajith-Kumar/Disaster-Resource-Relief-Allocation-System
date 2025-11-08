package com.disasterrelief.relief_system.repository;

import com.disasterrelief.relief_system.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUserId(Long userId);
    List<Request> findByStatus(Request.Status status);

    @Query("SELECT r FROM Request r WHERE r.status = 'PENDING' ORDER BY r.priority DESC, r.createdAt ASC")
    List<Request> findPendingOrderedByPriority();

    @Query("SELECT COUNT(r) FROM Request r WHERE r.status = ?1")
    Long countByStatus(Request.Status status);

    @Query("SELECT r FROM Request r WHERE r.userId = ?1 ORDER BY r.createdAt DESC")
    List<Request> findByUserIdOrderByCreatedAtDesc(Long userId);
}