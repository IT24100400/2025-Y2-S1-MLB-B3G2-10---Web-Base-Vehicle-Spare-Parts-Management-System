package com.vehicle.spareparts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "warranty_claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WarrantyClaim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String claimNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"password", "orders", "warranties", "deliveries"})
    private User customer;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"orderItems", "warranties"})
    private SparePart product;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"customer", "orderItems", "deliveries", "payments", "approvedBy"})
    private Order order;
    
    @Column(nullable = false)
    private LocalDate purchaseDate;
    
    @Column(nullable = false)
    private LocalDate warrantyExpiryDate;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String issueDescription;
    
    @Column(columnDefinition = "TEXT")
    private String customerComments;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyClaimStatus status = WarrantyClaimStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String storeResponse;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processed_by")
    @JsonIgnoreProperties({"password", "orders", "warranties", "deliveries"})
    private User processedBy;
    
    public enum WarrantyClaimStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        COMPLETED
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
