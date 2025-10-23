package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.AuditLog;
import com.vehicle.spareparts.entity.Order;
import com.vehicle.spareparts.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer - Audit Log
 * Records order events in audit log
 */
@Component
public class AuditLogObserver implements OrderObserver {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void update(Order order, String event) {
        // Create audit log entry
        AuditLog log = new AuditLog();
        log.setAction(event);
        log.setEntityType("ORDER");
        log.setEntityId(order.getId());
        log.setUser(order.getCustomer());

        // Store order details in newValue field
        String orderDetails = "Order " + order.getOrderNumber() + " - " + event +
                      ". Amount: $" + order.getTotalAmount() +
                      ", Status: " + order.getStatus();
        log.setNewValue(orderDetails);

        auditLogRepository.save(log);

        System.out.println("📋 AUDIT LOG CREATED");
        System.out.println("Action: " + event);
        System.out.println("Order: " + order.getOrderNumber());
        System.out.println("Details: " + orderDetails);
        System.out.println("-----------------------------------");
    }

    @Override
    public String getObserverName() {
        return "AUDIT_LOG";
    }
}
