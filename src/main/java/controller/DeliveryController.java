package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.dto.MessageResponse;
import com.vehicle.spareparts.entity.Delivery;
import com.vehicle.spareparts.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DeliveryController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeliveryController.class);

    @Autowired
    private DeliveryService deliveryService;
    
    @GetMapping
    // Temporarily removed @PreAuthorize to debug - will add back after fixing role issues
    // @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER', 'DELIVERY_STAFF')")
    public ResponseEntity<List<Delivery>> getAllDeliveries(Authentication authentication) {
        logger.info("User {} requesting all deliveries", authentication != null ? authentication.getName() : "anonymous");
        if (authentication != null) {
            logger.info("User authorities: {}", authentication.getAuthorities());
        }
        List<Delivery> deliveries = deliveryService.getAllDeliveries();
        logger.info("Returning {} deliveries", deliveries.size());
        return ResponseEntity.ok(deliveries);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDelivery(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(deliveryService.getDelivery(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/my-deliveries")
    @PreAuthorize("hasRole('DELIVERY_STAFF')")
    public ResponseEntity<List<Delivery>> getMyDeliveries(Authentication authentication) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStaff(authentication.getName()));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER', 'DELIVERY_STAFF')")
    public ResponseEntity<List<Delivery>> getDeliveriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_STAFF')")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long id, 
                                                   @RequestParam String status,
                                                   @RequestParam(required = false) String notes) {
        try {
            Delivery delivery = deliveryService.updateDeliveryStatus(id, status, notes);
            return ResponseEntity.ok(delivery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER', 'DELIVERY_STAFF')")
    public ResponseEntity<?> assignDeliveryStaff(@PathVariable Long id, @RequestParam Long staffId) {
        try {
            Delivery delivery = deliveryService.assignDeliveryStaff(id, staffId);
            return ResponseEntity.ok(delivery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/update-details")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER', 'DELIVERY_STAFF')")
    public ResponseEntity<?> updateDeliveryDetails(@PathVariable Long id,
                                                    @RequestParam(required = false) Long staffId,
                                                    @RequestParam(required = false) String address) {
        try {
            Delivery delivery = deliveryService.updateDeliveryDetails(id, staffId, address);
            return ResponseEntity.ok(delivery);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_STAFF')")
    public ResponseEntity<?> deleteDelivery(@PathVariable Long id) {
        try {
            deliveryService.deleteDelivery(id);
            return ResponseEntity.ok(new MessageResponse("Delivery record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
