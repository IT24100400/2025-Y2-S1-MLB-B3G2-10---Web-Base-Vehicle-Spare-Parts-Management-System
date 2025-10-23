package com.vehicle.spareparts.service;

import com.vehicle.spareparts.entity.*;
import com.vehicle.spareparts.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarrantyClaimService {
    
    private final WarrantyClaimRepository warrantyClaimRepository;
    private final OrderRepository orderRepository;
    private final SparePartRepository sparePartRepository;
    private final UserRepository userRepository;
    
    // CREATE - Customer creates warranty claim
    @Transactional
    public WarrantyClaim createClaim(Long customerId, Long orderId, Long productId, 
                                    String issueDescription, String customerComments) {
        
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        SparePart product = sparePartRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Validate order belongs to customer
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Order does not belong to customer");
        }
        
        // Validate order is delivered
        if (!"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("Order must be DELIVERED before claiming warranty. Current status: " + order.getStatus());
        }
        
        // Create warranty claim
        WarrantyClaim claim = new WarrantyClaim();
        claim.setClaimNumber("WC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        claim.setCustomer(customer);
        claim.setProduct(product);
        claim.setOrder(order);
        claim.setPurchaseDate(order.getOrderDate().toLocalDate());
        
        // Calculate warranty expiry (using product warranty period in months)
        int warrantyMonths = product.getWarrantyMonths() != null ? product.getWarrantyMonths() : 12;
        claim.setWarrantyExpiryDate(order.getOrderDate().toLocalDate().plusMonths(warrantyMonths));
        
        claim.setIssueDescription(issueDescription);
        claim.setCustomerComments(customerComments);
        claim.setStatus(WarrantyClaim.WarrantyClaimStatus.PENDING);
        claim.setCreatedAt(LocalDateTime.now());
        
        // Check if warranty expired
        if (LocalDate.now().isAfter(claim.getWarrantyExpiryDate())) {
            throw new RuntimeException("Warranty period has expired. Expiry date was: " + claim.getWarrantyExpiryDate());
        }
        
        return warrantyClaimRepository.save(claim);
    }
    
    // READ - Get all claims (for Store Owner/Admin)
    public List<WarrantyClaim> getAllClaims() {
        return warrantyClaimRepository.findAllByOrderByCreatedAtDesc();
    }
    
    // READ - Get customer's claims
    public List<WarrantyClaim> getCustomerClaims(Long customerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        return warrantyClaimRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }
    
    // READ - Get single claim by ID
    public WarrantyClaim getClaimById(Long claimId) {
        return warrantyClaimRepository.findById(claimId)
            .orElseThrow(() -> new RuntimeException("Warranty claim not found"));
    }
    
    // READ - Get claims by status
    public List<WarrantyClaim> getClaimsByStatus(WarrantyClaim.WarrantyClaimStatus status) {
        return warrantyClaimRepository.findByStatus(status);
    }
    
    // UPDATE - Customer updates claim (only if PENDING)
    @Transactional
    public WarrantyClaim updateClaim(Long claimId, String issueDescription, String customerComments) {
        WarrantyClaim claim = getClaimById(claimId);
        
        // Only allow update if status is PENDING
        if (claim.getStatus() != WarrantyClaim.WarrantyClaimStatus.PENDING) {
            throw new RuntimeException("Can only update PENDING claims. Current status: " + claim.getStatus());
        }
        
        if (issueDescription != null && !issueDescription.trim().isEmpty()) {
            claim.setIssueDescription(issueDescription);
        }
        if (customerComments != null && !customerComments.trim().isEmpty()) {
            claim.setCustomerComments(customerComments);
        }
        claim.setUpdatedAt(LocalDateTime.now());
        
        return warrantyClaimRepository.save(claim);
    }
    
    // UPDATE - Store owner approves claim
    @Transactional
    public WarrantyClaim approveClaim(Long claimId, Long storeOwnerId, String response) {
        WarrantyClaim claim = getClaimById(claimId);
        User storeOwner = userRepository.findById(storeOwnerId)
            .orElseThrow(() -> new RuntimeException("Store owner not found"));
        
        claim.setStatus(WarrantyClaim.WarrantyClaimStatus.APPROVED);
        claim.setStoreResponse(response);
        claim.setProcessedBy(storeOwner);
        claim.setProcessedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        
        return warrantyClaimRepository.save(claim);
    }
    
    // UPDATE - Store owner rejects claim
    @Transactional
    public WarrantyClaim rejectClaim(Long claimId, Long storeOwnerId, String reason) {
        WarrantyClaim claim = getClaimById(claimId);
        User storeOwner = userRepository.findById(storeOwnerId)
            .orElseThrow(() -> new RuntimeException("Store owner not found"));
        
        claim.setStatus(WarrantyClaim.WarrantyClaimStatus.REJECTED);
        claim.setStoreResponse(reason);
        claim.setProcessedBy(storeOwner);
        claim.setProcessedAt(LocalDateTime.now());
        claim.setUpdatedAt(LocalDateTime.now());
        
        return warrantyClaimRepository.save(claim);
    }
    
    // DELETE - Customer deletes pending claim
    @Transactional
    public void deleteClaim(Long claimId, Long customerId) {
        WarrantyClaim claim = getClaimById(claimId);
        
        // Validate customer owns the claim
        if (!claim.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Not authorized to delete this claim");
        }
        
        // Only allow delete if status is PENDING
        if (claim.getStatus() != WarrantyClaim.WarrantyClaimStatus.PENDING) {
            throw new RuntimeException("Can only delete PENDING claims. Current status: " + claim.getStatus());
        }
        
        warrantyClaimRepository.delete(claim);
    }
}
