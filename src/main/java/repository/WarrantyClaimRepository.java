package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.WarrantyClaim;
import com.vehicle.spareparts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, Long> {
    List<WarrantyClaim> findByCustomer(User customer);
    List<WarrantyClaim> findByStatus(WarrantyClaim.WarrantyClaimStatus status);
    List<WarrantyClaim> findByCustomerOrderByCreatedAtDesc(User customer);
    List<WarrantyClaim> findAllByOrderByCreatedAtDesc();
    boolean existsByClaimNumber(String claimNumber);
}
