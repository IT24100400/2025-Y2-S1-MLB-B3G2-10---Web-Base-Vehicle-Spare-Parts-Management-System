package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.dto.MessageResponse;
import com.vehicle.spareparts.dto.WarrantyClaimRequest;
import com.vehicle.spareparts.dto.WarrantyResponse;
import com.vehicle.spareparts.service.WarrantyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warranties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WarrantyController {
    
    @Autowired
    private WarrantyService warrantyService;
    
    @GetMapping("/my-warranties")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<WarrantyResponse>> getMyWarranties(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(warrantyService.getCustomerWarranties(username));
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<WarrantyResponse>> getMyActiveWarranties(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(warrantyService.getActiveWarranties(username));
    }
    
    @GetMapping("/{warrantyNumber}")
    public ResponseEntity<WarrantyResponse> getWarrantyByNumber(@PathVariable String warrantyNumber) {
        try {
            return ResponseEntity.ok(warrantyService.getWarrantyByNumber(warrantyNumber));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<List<WarrantyResponse>> getAllWarranties() {
        return ResponseEntity.ok(warrantyService.getAllWarranties());
    }
    
    @GetMapping("/pending-claims")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<List<WarrantyResponse>> getPendingClaims() {
        return ResponseEntity.ok(warrantyService.getPendingClaims());
    }
    
    @PostMapping("/{id}/claim")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> fileWarrantyClaim(
            @PathVariable Long id,
            @RequestBody WarrantyClaimRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            WarrantyResponse response = warrantyService.fileWarrantyClaim(id, request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/approve-claim")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> approveClaim(@PathVariable Long id) {
        try {
            WarrantyResponse response = warrantyService.approveWarrantyClaim(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/reject-claim")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> rejectClaim(@PathVariable Long id, @RequestBody WarrantyClaimRequest request) {
        try {
            WarrantyResponse response = warrantyService.rejectWarrantyClaim(id, request.getNotes());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> getWarrantyStats() {
        return ResponseEntity.ok(warrantyService.getWarrantyStatistics());
    }
    
    // CREATE - Manual warranty creation (Admin/Store Owner)
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> createWarranty(@RequestBody WarrantyClaimRequest request) {
        try {
            WarrantyResponse response = warrantyService.createWarrantyManually(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // UPDATE - Update warranty
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> updateWarranty(@PathVariable Long id, @RequestBody WarrantyClaimRequest request) {
        try {
            WarrantyResponse response = warrantyService.updateWarranty(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // DELETE - Delete warranty
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteWarranty(@PathVariable Long id) {
        try {
            warrantyService.deleteWarranty(id);
            return ResponseEntity.ok(new MessageResponse("Warranty deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Get warranties expiring soon
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<List<WarrantyResponse>> getExpiringWarranties(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(warrantyService.getExpiringWarranties(days));
    }

    // Check if warranty is valid
    @GetMapping("/{id}/valid")
    public ResponseEntity<?> checkWarrantyValidity(@PathVariable Long id) {
        try {
            boolean isValid = warrantyService.isWarrantyValid(id);
            return ResponseEntity.ok(new MessageResponse(
                isValid ? "Warranty is valid" : "Warranty is not valid or expired"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
