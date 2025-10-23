package com.vehicle.spareparts.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartRequest {
    
    @NotBlank(message = "Part number is required")
    private String partNumber;
    
    @NotBlank(message = "Part name is required")
    private String partName;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String brand;
    
    private String vehicleModel;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    private Integer reorderLevel = 10;
    
    private Integer warrantyMonths = 6;
    
    private String imageUrl;
    
    private Boolean isActive = true;
}
