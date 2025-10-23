package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserId(Long userId);
    
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
