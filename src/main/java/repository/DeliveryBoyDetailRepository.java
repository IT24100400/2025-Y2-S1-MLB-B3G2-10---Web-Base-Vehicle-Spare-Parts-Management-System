package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.DeliveryBoyDetail;
import com.vehicle.spareparts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeliveryBoyDetailRepository extends JpaRepository<DeliveryBoyDetail, Long> {
    Optional<DeliveryBoyDetail> findByUserId(Long userId);
    Optional<DeliveryBoyDetail> findByUser(User user);
    boolean existsByVehicleNumber(String vehicleNumber);
    boolean existsByLicenseNumber(String licenseNumber);
}