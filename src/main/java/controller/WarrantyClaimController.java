package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.entity.WarrantyClaim;
import com.vehicle.spareparts.service.WarrantyClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warranty-claims")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarrantyClaimController {
    
    private final WarrantyClaimService warrantyClaimService;
    
    // CREATE - Customer creates warranty claim
    @PostMapping("/create")
    public ResponseEntity<?> createClaim(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = Long.parseLong(request.get("customerId").toString());
            Long orderId = Long.parseLong(request.get("orderId").toString());
            Long productId = Long.parseLong(request.get("productId").toString());
            String issueDescription = request.get("issueDescription").toString();
            String customerComments = request.getOrDefault("customerComments", "").toString();
            
            WarrantyClaim claim = warrantyClaimService.createClaim(
                customerId, orderId, productId, issueDescription, customerComments
            );
            
            return ResponseEntity.ok(claim);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    // READ - Get all claims (Store Owner/Admin)
    @GetMapping("/all")
    public ResponseEntity<List<WarrantyClaim>> getAllClaims() {
        return ResponseEntity.ok(warrantyClaimService.getAllClaims());
    }
    
    // READ - Get customer's claims
    @GetMapping("/my-claims/{customerId}")
    public ResponseEntity<List<WarrantyClaim>> getMyClaims(@PathVariable Long customerId) {
        return ResponseEntity.ok(warrantyClaimService.getCustomerClaims(customerId));
    }
    
    // READ - Get single claim
    @GetMapping("/{claimId}")
    public ResponseEntity<WarrantyClaim> getClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(warrantyClaimService.getClaimById(claimId));
    }
    
    // READ - Get claims by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WarrantyClaim>> getClaimsByStatus(@PathVariable String status) {
        WarrantyClaim.WarrantyClaimStatus claimStatus = WarrantyClaim.WarrantyClaimStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(warrantyClaimService.getClaimsByStatus(claimStatus));
    }
    
    // UPDATE - Customer updates claim
    @PutMapping("/{claimId}")
    public ResponseEntity<?> updateClaim(
            @PathVariable Long claimId,
            @RequestBody Map<String, String> request) {
        try {
            String issueDescription = request.get("issueDescription");
            String customerComments = request.get("customerComments");
            
            WarrantyClaim updated = warrantyClaimService.updateClaim(
                claimId, issueDescription, customerComments
            );
            
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    // UPDATE - Store owner approves claim
    @PutMapping("/{claimId}/approve")
    public ResponseEntity<?> approveClaim(
            @PathVariable Long claimId,
            @RequestBody Map<String, Object> request) {
        try {
            Long storeOwnerId = Long.parseLong(request.get("storeOwnerId").toString());
            String response = request.get("response").toString();
            
            WarrantyClaim approved = warrantyClaimService.approveClaim(
                claimId, storeOwnerId, response
            );
            
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    // UPDATE - Store owner rejects claim
    @PutMapping("/{claimId}/reject")
    public ResponseEntity<?> rejectClaim(
            @PathVariable Long claimId,
            @RequestBody Map<String, Object> request) {
        try {
            Long storeOwnerId = Long.parseLong(request.get("storeOwnerId").toString());
            String reason = request.get("reason").toString();
            
            WarrantyClaim rejected = warrantyClaimService.rejectClaim(
                claimId, storeOwnerId, reason
            );
            
            return ResponseEntity.ok(rejected);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    // DELETE - Customer deletes pending claim
    @DeleteMapping("/{claimId}/customer/{customerId}")
    public ResponseEntity<?> deleteClaim(
            @PathVariable Long claimId,
            @PathVariable Long customerId) {
        try {
            warrantyClaimService.deleteClaim(claimId, customerId);
            return ResponseEntity.ok(Map.of("message", "Warranty claim deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
}
