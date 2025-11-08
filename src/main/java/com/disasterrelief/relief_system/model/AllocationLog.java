package com.disasterrelief.relief_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "allocation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocationLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private Long requestId;

    private Long resourceId;

    private Integer quantity;

    private String notes;

    @CreationTimestamp
    private LocalDateTime allocatedTime;
}
