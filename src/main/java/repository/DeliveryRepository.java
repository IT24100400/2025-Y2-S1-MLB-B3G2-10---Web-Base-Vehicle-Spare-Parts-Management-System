package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);
    
    Optional<Delivery> findByOrderId(Long orderId);
    
    List<Delivery> findByStatus(String status);
    
    @Query("SELECT d FROM Delivery d WHERE d.deliveryStaff.id = :staffId ORDER BY d.assignedAt DESC")
    List<Delivery> findDeliveriesByStaff(@Param("staffId") Long staffId);
    
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.status = :status")
    Long countByStatus(@Param("status") String status);
}
