package com.vehicle.spareparts.service;

import com.vehicle.spareparts.dto.DeliveryBoyProfileUpdateRequest;
import com.vehicle.spareparts.dto.DeliveryBoyRegisterRequest;
import com.vehicle.spareparts.dto.DeliveryBoyResponse;
import com.vehicle.spareparts.entity.DeliveryBoyDetail;
import com.vehicle.spareparts.entity.Role;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.DeliveryBoyDetailRepository;
import com.vehicle.spareparts.repository.RoleRepository;
import com.vehicle.spareparts.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryBoyService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeliveryBoyService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private DeliveryBoyDetailRepository deliveryBoyDetailRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Register a new delivery boy
    public DeliveryBoyResponse registerDeliveryBoy(DeliveryBoyRegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        
        // Check if vehicle number already exists
        if (deliveryBoyDetailRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new RuntimeException("Vehicle number already registered!");
        }
        
        // Check if license number already exists
        if (deliveryBoyDetailRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already registered!");
        }
        
        // Get DELIVERY_STAFF role
        Role deliveryRole = roleRepository.findByName("DELIVERY_STAFF")
                .orElseThrow(() -> new RuntimeException("Delivery staff role not found"));
        
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(deliveryRole);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Create delivery boy details
        DeliveryBoyDetail deliveryBoyDetail = new DeliveryBoyDetail();
        deliveryBoyDetail.setUser(savedUser);
        deliveryBoyDetail.setVehicleMake(request.getVehicleMake());
        deliveryBoyDetail.setVehicleModel(request.getVehicleModel());
        deliveryBoyDetail.setVehicleNumber(request.getVehicleNumber());
        deliveryBoyDetail.setLicenseNumber(request.getLicenseNumber());
        deliveryBoyDetail.setDrivingExperience(request.getDrivingExperience());
        
        DeliveryBoyDetail savedDetail = deliveryBoyDetailRepository.save(deliveryBoyDetail);
        
        return mapToResponse(savedUser, savedDetail);
    }
    
    // Get delivery boy by ID
    public DeliveryBoyResponse getDeliveryBoyById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy not found with id: " + id));
        
        DeliveryBoyDetail detail = deliveryBoyDetailRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy details not found for user id: " + id));
        
        return mapToResponse(user, detail);
    }
    
    // Get all delivery boys
    public List<DeliveryBoyResponse> getAllDeliveryBoys() {
        logger.info("Fetching all delivery boys");
        try {
            List<User> deliveryUsers = userRepository.findByRoleName("DELIVERY_STAFF");
            logger.info("Found {} users with DELIVERY_STAFF role", deliveryUsers.size());
            return deliveryUsers.stream().map(user -> {
                DeliveryBoyDetail detail = deliveryBoyDetailRepository.findByUserId(user.getId())
                        .orElse(null);
                return mapToResponse(user, detail);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching delivery boys", e);
            throw e;
        }
    }
    
    // Update delivery boy profile
    public DeliveryBoyResponse updateDeliveryBoyProfile(Long id, DeliveryBoyProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy not found with id: " + id));
        
        DeliveryBoyDetail detail = deliveryBoyDetailRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy details not found for user id: " + id));
        
        // Update user information
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        
        // Update delivery boy details
        detail.setVehicleMake(request.getVehicleMake());
        detail.setVehicleModel(request.getVehicleModel());
        detail.setVehicleNumber(request.getVehicleNumber());
        detail.setLicenseNumber(request.getLicenseNumber());
        detail.setDrivingExperience(request.getDrivingExperience());
        
        User updatedUser = userRepository.save(user);
        DeliveryBoyDetail updatedDetail = deliveryBoyDetailRepository.save(detail);
        
        return mapToResponse(updatedUser, updatedDetail);
    }
    
    // Deactivate delivery boy
    public DeliveryBoyResponse deactivateDeliveryBoy(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy not found with id: " + id));
        
        user.setIsActive(false);
        User updatedUser = userRepository.save(user);
        
        DeliveryBoyDetail detail = deliveryBoyDetailRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy details not found for user id: " + id));
        
        return mapToResponse(updatedUser, detail);
    }
    
    // Activate delivery boy
    public DeliveryBoyResponse activateDeliveryBoy(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy not found with id: " + id));
        
        user.setIsActive(true);
        User updatedUser = userRepository.save(user);
        
        DeliveryBoyDetail detail = deliveryBoyDetailRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Delivery boy details not found for user id: " + id));
        
        return mapToResponse(updatedUser, detail);
    }
    
    // Helper method to map entities to response DTO
    private DeliveryBoyResponse mapToResponse(User user, DeliveryBoyDetail detail) {
        DeliveryBoyResponse response = new DeliveryBoyResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        if (detail != null) {
            response.setVehicleMake(detail.getVehicleMake());
            response.setVehicleModel(detail.getVehicleModel());
            response.setVehicleNumber(detail.getVehicleNumber());
            response.setLicenseNumber(detail.getLicenseNumber());
            response.setDrivingExperience(detail.getDrivingExperience());
        }
        
        return response;
    }
}