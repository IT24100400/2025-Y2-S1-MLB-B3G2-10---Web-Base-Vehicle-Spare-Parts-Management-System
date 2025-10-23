package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.dto.DeliveryBoyRegisterRequest;
import com.vehicle.spareparts.dto.DeliveryBoyResponse;
import com.vehicle.spareparts.entity.DeliveryBoyDetail;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.DeliveryBoyDetailRepository;
import com.vehicle.spareparts.repository.UserRepository;
import com.vehicle.spareparts.service.DeliveryBoyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test/delivery-boys")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DeliveryBoyTestController {
    
    @Autowired
    private DeliveryBoyService deliveryBoyService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DeliveryBoyDetailRepository deliveryBoyDetailRepository;
    
    // Test endpoint to create a sample delivery boy
    @PostMapping("/create-sample")
    public ResponseEntity<DeliveryBoyResponse> createSampleDeliveryBoy() {
        DeliveryBoyRegisterRequest request = new DeliveryBoyRegisterRequest();
        request.setUsername("deliveryboy1");
        request.setEmail("delivery1@test.com");
        request.setPassword("password123");
        request.setFullName("Test Delivery Boy");
        request.setPhone("1234567890");
        request.setAddress("123 Test Street, Test City");
        request.setVehicleMake("Toyota");
        request.setVehicleModel("Corolla");
        request.setVehicleNumber("TEST123");
        request.setLicenseNumber("DL1234567890");
        request.setDrivingExperience(5);
        
        DeliveryBoyResponse response = deliveryBoyService.registerDeliveryBoy(request);
        return ResponseEntity.ok(response);
    }
    
    // Test endpoint to get all users with DELIVERY_STAFF role
    @GetMapping("/test-users")
    public ResponseEntity<List<User>> testGetDeliveryStaffUsers() {
        List<User> users = userRepository.findByRoleName("DELIVERY_STAFF");
        return ResponseEntity.ok(users);
    }
    
    // Test endpoint to get all delivery boy details
    @GetMapping("/test-details")
    public ResponseEntity<List<DeliveryBoyDetail>> testGetDeliveryBoyDetails() {
        List<DeliveryBoyDetail> details = deliveryBoyDetailRepository.findAll();
        return ResponseEntity.ok(details);
    }
}