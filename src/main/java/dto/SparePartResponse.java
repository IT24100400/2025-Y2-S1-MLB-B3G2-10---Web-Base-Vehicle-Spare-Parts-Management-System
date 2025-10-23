package com.vehicle.spareparts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartResponse {
    private Long id;
    private String partNumber;
    private String partName;
    private String category;
    private String brand;
    private String vehicleModel;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Integer warrantyMonths;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
