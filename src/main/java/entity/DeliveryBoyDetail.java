package com.vehicle.spareparts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_boy_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "vehicle_make", nullable = false, length = 50)
    private String vehicleMake;
    
    @Column(name = "vehicle_model", nullable = false, length = 50)
    private String vehicleModel;
    
    @Column(name = "vehicle_number", nullable = false, length = 20)
    private String vehicleNumber;
    
    @Column(name = "license_number", nullable = false, length = 50)
    private String licenseNumber;
    
    @Column(name = "driving_experience", nullable = false)
    private Integer drivingExperience;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}