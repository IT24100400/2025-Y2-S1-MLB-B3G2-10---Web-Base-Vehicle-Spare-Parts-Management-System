package com.vehicle.spareparts.service;

import com.vehicle.spareparts.dto.WarrantyResponse;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.entity.Warranty;
import com.vehicle.spareparts.repository.UserRepository;
import com.vehicle.spareparts.repository.WarrantyRepository;
import com.vehicle.spareparts.observer.WarrantySubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WarrantyService {
    
    @Autowired
    private WarrantyRepository warrantyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // OBSERVER PATTERN - Warranty Notifications
    @Autowired
    private WarrantySubject warrantySubject;

    public List<WarrantyResponse> getCustomerWarranties(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Warranty> warranties = warrantyRepository.findByCustomerId(user.getId());
        return warranties.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<WarrantyResponse> getActiveWarranties(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Warranty> warranties = warrantyRepository.findActiveWarrantiesByCustomer(user.getId());
        return warranties.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public WarrantyResponse getWarrantyByNumber(String warrantyNumber) {
        Warranty warranty = warrantyRepository.findByWarrantyNumber(warrantyNumber)
                .orElseThrow(() -> new RuntimeException("Warranty not found"));
        return convertToResponse(warranty);
    }
    
    public List<WarrantyResponse> getAllWarranties() {
        List<Warranty> warranties = warrantyRepository.findAll();
        return warranties.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<WarrantyResponse> getPendingClaims() {
        List<Warranty> warranties = warrantyRepository.findAll();
        return warranties.stream()
                .filter(w -> "PENDING".equals(w.getClaimStatus()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public WarrantyResponse fileWarrantyClaim(Long warrantyId, com.vehicle.spareparts.dto.WarrantyClaimRequest request, String username) {
        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new RuntimeException("Warranty not found"));
        
        // Verify ownership
        if (!warranty.getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: This warranty does not belong to you");
        }
        
        // Check if warranty is active
        if (!"ACTIVE".equals(warranty.getStatus())) {
            throw new RuntimeException("Warranty is not active");
        }
        
        // Check if warranty is expired
        if (warranty.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Warranty has expired");
        }
        
        // Check if claim already exists
        if (warranty.getClaimStatus() != null) {
            throw new RuntimeException("Claim already filed for this warranty. Status: " + warranty.getClaimStatus());
        }
        
        // File the claim
        warranty.setClaimStatus("PENDING");
        warranty.setClaimDate(LocalDateTime.now());
        warranty.setClaimNotes(request.getNotes());
        
        Warranty saved = warrantyRepository.save(warranty);

        // OBSERVER PATTERN - Notify observers about warranty claim
        warrantySubject.notifyObservers(saved, "WARRANTY_CLAIM_FILED");

        return convertToResponse(saved);
    }
    
    @Transactional
    public WarrantyResponse approveWarrantyClaim(Long warrantyId) {
        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new RuntimeException("Warranty not found"));
        
        if (!"PENDING".equals(warranty.getClaimStatus())) {
            throw new RuntimeException("Only pending claims can be approved");
        }
        
        warranty.setClaimStatus("APPROVED");
        warranty.setStatus("CLAIMED");
        warranty.setUpdatedAt(LocalDateTime.now());

        Warranty saved = warrantyRepository.save(warranty);

        System.out.println("✅ WARRANTY CLAIM APPROVED");
        System.out.println("Warranty: " + warranty.getWarrantyNumber());
        System.out.println("Customer: " + warranty.getCustomer().getFullName());
        System.out.println("Part: " + warranty.getSparePart().getPartName());

        // OBSERVER PATTERN - Notify observers about claim approval
        warrantySubject.notifyObservers(saved, "WARRANTY_CLAIM_APPROVED");

        return convertToResponse(saved);
    }
    
    @Transactional
    public WarrantyResponse rejectWarrantyClaim(Long warrantyId, String rejectionNotes) {
        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new RuntimeException("Warranty not found"));
        
        if (!"PENDING".equals(warranty.getClaimStatus())) {
            throw new RuntimeException("Only pending claims can be rejected");
        }
        
        warranty.setClaimStatus("REJECTED");
        warranty.setClaimNotes(warranty.getClaimNotes() + "\n\nREJECTION REASON: " + rejectionNotes);
        warranty.setUpdatedAt(LocalDateTime.now());

        Warranty saved = warrantyRepository.save(warranty);

        System.out.println("❌ WARRANTY CLAIM REJECTED");
        System.out.println("Warranty: " + warranty.getWarrantyNumber());
        System.out.println("Reason: " + rejectionNotes);

        // OBSERVER PATTERN - Notify observers about claim rejection
        warrantySubject.notifyObservers(saved, "WARRANTY_CLAIM_REJECTED");

        return convertToResponse(saved);
    }
    
    public Map<String, Object> getWarrantyStatistics() {
        List<Warranty> allWarranties = warrantyRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        
        long totalWarranties = allWarranties.size();
        long activeWarranties = allWarranties.stream()
                .filter(w -> "ACTIVE".equals(w.getStatus()))
                .count();
        long expiredWarranties = allWarranties.stream()
                .filter(w -> "EXPIRED".equals(w.getStatus()))
                .count();
        long claimedWarranties = allWarranties.stream()
                .filter(w -> "CLAIMED".equals(w.getStatus()))
                .count();
        
        long pendingClaims = allWarranties.stream()
                .filter(w -> "PENDING".equals(w.getClaimStatus()))
                .count();
        long approvedClaims = allWarranties.stream()
                .filter(w -> "APPROVED".equals(w.getClaimStatus()))
                .count();
        long rejectedClaims = allWarranties.stream()
                .filter(w -> "REJECTED".equals(w.getClaimStatus()))
                .count();
        
        stats.put("totalWarranties", totalWarranties);
        stats.put("activeWarranties", activeWarranties);
        stats.put("expiredWarranties", expiredWarranties);
        stats.put("claimedWarranties", claimedWarranties);
        stats.put("pendingClaims", pendingClaims);
        stats.put("approvedClaims", approvedClaims);
        stats.put("rejectedClaims", rejectedClaims);

        if (totalWarranties > 0) {
            double claimRate = (double) (approvedClaims + rejectedClaims + pendingClaims) / totalWarranties * 100;
            stats.put("claimRate", String.format("%.2f%%", claimRate));
        } else {
            stats.put("claimRate", "0.00%");
        }

        return stats;
    }
    
    public boolean isWarrantyValid(Long warrantyId) {
        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new RuntimeException("Warranty not found"));

        return "ACTIVE".equals(warranty.getStatus()) &&
               warranty.getExpiryDate().isAfter(LocalDate.now());
    }

    public List<WarrantyResponse> getExpiringWarranties(int days) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        List<Warranty> warranties = warrantyRepository.findAll();

        return warranties.stream()
                .filter(w -> "ACTIVE".equals(w.getStatus()))
                .filter(w -> w.getExpiryDate().isAfter(LocalDate.now()) &&
                            w.getExpiryDate().isBefore(futureDate))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private WarrantyResponse convertToResponse(Warranty warranty) {
        WarrantyResponse response = new WarrantyResponse();
        response.setId(warranty.getId());
        response.setWarrantyNumber(warranty.getWarrantyNumber());
        response.setCustomerName(warranty.getCustomer().getFullName());
        response.setSparePartName(warranty.getSparePart().getPartName());
        response.setPartNumber(warranty.getSparePart().getPartNumber());
        response.setPurchaseDate(warranty.getPurchaseDate());
        response.setExpiryDate(warranty.getExpiryDate());
        response.setStatus(warranty.getStatus());
        response.setClaimStatus(warranty.getClaimStatus());
        response.setClaimDate(warranty.getClaimDate());
        response.setClaimNotes(warranty.getClaimNotes());
        
        // Calculate days remaining
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), warranty.getExpiryDate());
        response.setDaysRemaining((int) daysRemaining);

        // Check if expired
        if (warranty.getExpiryDate().isBefore(LocalDate.now())) {
            response.setIsExpired(true);
            response.setCanClaim(false);
        } else {
            response.setIsExpired(false);
            response.setCanClaim("ACTIVE".equals(warranty.getStatus()) && warranty.getClaimStatus() == null);
        }

        return response;
    }
    
    // CREATE - Manual warranty creation
    @Transactional
    public WarrantyResponse createWarrantyManually(com.vehicle.spareparts.dto.WarrantyClaimRequest request) {
        // This method allows admin/store owner to manually create a warranty
        // You would need to add fields to WarrantyClaimRequest for creation:
        // customerId, sparePartId, purchaseDate, warrantyPeriodMonths
        
        Warranty warranty = new Warranty();
        warranty.setWarrantyNumber("WMN" + System.currentTimeMillis()); // Manual warranty number
        warranty.setPurchaseDate(LocalDate.now());
        warranty.setExpiryDate(LocalDate.now().plusMonths(12)); // Default 12 months
        warranty.setStatus("ACTIVE");
        warranty.setCreatedAt(LocalDateTime.now());
        
        // Note: You need to set customer and sparePart from request
        // This is a placeholder - implement based on your WarrantyClaimRequest structure
        
        Warranty savedWarranty = warrantyRepository.save(warranty);
        return convertToResponse(savedWarranty);
    }
    
    // UPDATE - Update warranty details
    @Transactional
    public WarrantyResponse updateWarranty(Long id, com.vehicle.spareparts.dto.WarrantyClaimRequest request) {
        Warranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warranty not found"));
        
        // Update warranty details
        if (request.getNotes() != null) {
            warranty.setClaimNotes(request.getNotes());
        }
        
        // Admin can update status and other fields
        warranty.setUpdatedAt(LocalDateTime.now());
        
        Warranty updatedWarranty = warrantyRepository.save(warranty);
        return convertToResponse(updatedWarranty);
    }
    
    // DELETE - Delete warranty (Admin only for cleanup or error correction)
    @Transactional
    public void deleteWarranty(Long id) {
        Warranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warranty not found with ID: " + id));
        
        // Check if warranty has any claims - maybe prevent deletion if claimed
        if (warranty.getClaimStatus() != null && !"REJECTED".equals(warranty.getClaimStatus())) {
            throw new RuntimeException("Cannot delete warranty with active or approved claims");
        }
        
        warrantyRepository.delete(warranty);
    }
}
