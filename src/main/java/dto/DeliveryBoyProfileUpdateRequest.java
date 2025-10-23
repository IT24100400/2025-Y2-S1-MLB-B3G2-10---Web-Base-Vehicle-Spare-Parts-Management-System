package com.vehicle.spareparts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyProfileUpdateRequest {
    
    // User Information
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private String phone;
    
    private String address;
    
    // Vehicle Information
    @NotBlank(message = "Vehicle make is required")
    private String vehicleMake;
    
    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;
    
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @NotNull(message = "Years of driving experience is required")
    private Integer drivingExperience;
}