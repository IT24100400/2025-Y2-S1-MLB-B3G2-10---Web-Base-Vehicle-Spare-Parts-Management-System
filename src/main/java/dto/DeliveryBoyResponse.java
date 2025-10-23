package com.vehicle.spareparts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyResponse {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Vehicle Information
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleNumber;
    private String licenseNumber;
    private Integer drivingExperience;
}