package com.vehicle.spareparts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyResponse {
    private Long id;
    private String warrantyNumber;
    private String customerName;
    private String sparePartName;
    private String partNumber;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String status;
    private String claimStatus;
    private LocalDateTime claimDate;
    private String claimNotes;
    private Integer daysRemaining;
    private Boolean isExpired;
    private Boolean canClaim;
}
