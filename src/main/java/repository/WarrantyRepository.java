package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    
    Optional<Warranty> findByWarrantyNumber(String warrantyNumber);
    
    List<Warranty> findByCustomerId(Long customerId);
    
    @Query("SELECT w FROM Warranty w WHERE w.customer.id = :customerId AND w.status = 'ACTIVE'")
    List<Warranty> findActiveWarrantiesByCustomer(@Param("customerId") Long customerId);
    
    @Query("SELECT w FROM Warranty w WHERE w.expiryDate < :date AND w.status = 'ACTIVE'")
    List<Warranty> findExpiredWarranties(@Param("date") LocalDate date);
}
