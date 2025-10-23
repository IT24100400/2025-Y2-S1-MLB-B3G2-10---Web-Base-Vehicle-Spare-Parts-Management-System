package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.dto.DeliveryBoyProfileUpdateRequest;
import com.vehicle.spareparts.dto.DeliveryBoyRegisterRequest;
import com.vehicle.spareparts.dto.DeliveryBoyResponse;
import com.vehicle.spareparts.dto.MessageResponse;
import com.vehicle.spareparts.service.DeliveryBoyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-boys")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DeliveryBoyController {
    
    @Autowired
    private DeliveryBoyService deliveryBoyService;
    
    // Register a new delivery boy (public endpoint)
    @PostMapping("/register")
    public ResponseEntity<?> registerDeliveryBoy(@Valid @RequestBody DeliveryBoyRegisterRequest request) {
        try {
            DeliveryBoyResponse response = deliveryBoyService.registerDeliveryBoy(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get all delivery boys (accessible to authenticated users)
    @GetMapping
    public ResponseEntity<List<DeliveryBoyResponse>> getAllDeliveryBoys() {
        try {
            List<DeliveryBoyResponse> deliveryBoys = deliveryBoyService.getAllDeliveryBoys();
            return ResponseEntity.ok(deliveryBoys);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }
    
    // Get delivery boy by ID (admin/staff only)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_OWNER')")
    public ResponseEntity<?> getDeliveryBoyById(@PathVariable Long id) {
        try {
            DeliveryBoyResponse response = deliveryBoyService.getDeliveryBoyById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Update delivery boy profile (delivery boy can update their own profile)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DELIVERY_STAFF') or hasRole('ADMIN') or hasRole('STORE_OWNER')")
    public ResponseEntity<?> updateDeliveryBoyProfile(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryBoyProfileUpdateRequest request) {
        try {
            // In a real application, you would check if the authenticated user
            // is authorized to update this profile
            DeliveryBoyResponse response = deliveryBoyService.updateDeliveryBoyProfile(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Deactivate delivery boy (admin only)
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateDeliveryBoy(@PathVariable Long id) {
        try {
            DeliveryBoyResponse response = deliveryBoyService.deactivateDeliveryBoy(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Activate delivery boy (admin only)
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateDeliveryBoy(@PathVariable Long id) {
        try {
            DeliveryBoyResponse response = deliveryBoyService.activateDeliveryBoy(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}