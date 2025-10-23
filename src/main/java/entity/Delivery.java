package com.vehicle.spareparts.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Delivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "delivery_number", nullable = false, unique = true, length = 50)
    private String deliveryNumber;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"deliveries", "payments", "customer", "orderItems", "approvedBy"})
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_staff_id")
    @JsonIgnoreProperties({"password", "deliveries", "orders"})
    private User deliveryStaff;
    
    @Column(nullable = false, length = 30)
    private String status = "PENDING";
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;
    
    @Column(name = "tracking_notes", columnDefinition = "TEXT")
    private String trackingNotes;
    
    @Column(name = "customer_signature")
    private String customerSignature;
    
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
