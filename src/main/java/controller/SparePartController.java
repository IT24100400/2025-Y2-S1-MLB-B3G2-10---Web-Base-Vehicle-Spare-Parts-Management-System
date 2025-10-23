package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.dto.MessageResponse;
import com.vehicle.spareparts.dto.SparePartRequest;
import com.vehicle.spareparts.dto.SparePartResponse;
import com.vehicle.spareparts.service.SparePartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/spareparts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SparePartController {
    
    @Autowired
    private SparePartService sparePartService;
    
    @GetMapping
    public ResponseEntity<List<SparePartResponse>> getAllSpareParts() {
        return ResponseEntity.ok(sparePartService.getActiveSpareParts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SparePartResponse> getSparePart(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sparePartService.getSparePart(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SparePartResponse>> searchSpareParts(@RequestParam String keyword) {
        return ResponseEntity.ok(sparePartService.searchSpareParts(keyword));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SparePartResponse>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(sparePartService.getSparePartsByCategory(category));
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<List<SparePartResponse>> getLowStockParts() {
        return ResponseEntity.ok(sparePartService.getLowStockParts());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> createSparePart(@Valid @RequestBody SparePartRequest request) {
        try {
            SparePartResponse response = sparePartService.createSparePart(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> updateSparePart(@PathVariable Long id, @Valid @RequestBody SparePartRequest request) {
        try {
            SparePartResponse response = sparePartService.updateSparePart(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> deleteSparePart(@PathVariable Long id) {
        try {
            sparePartService.deleteSparePart(id);
            return ResponseEntity.ok(new MessageResponse("Spare part deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
