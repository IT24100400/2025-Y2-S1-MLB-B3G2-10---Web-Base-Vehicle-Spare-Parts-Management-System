package com.vehicle.spareparts.strategy.delivery;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Concrete Strategy - Standard Delivery Implementation
 */
@Component
public class StandardDelivery implements DeliveryStrategy {

    private static final BigDecimal BASE_COST = new BigDecimal("10.00");
    private static final int DELIVERY_DAYS = 5;

    @Override
    public BigDecimal calculateDeliveryCost(Order order) {
        // Standard flat rate delivery
        System.out.println("Calculating Standard Delivery cost for Order: " + order.getOrderNumber());
        return BASE_COST;
    }

    @Override
    public LocalDateTime getEstimatedDeliveryTime() {
        // 5-7 business days
        return LocalDateTime.now().plusDays(DELIVERY_DAYS);
    }

    @Override
    public String getDeliveryMethodName() {
        return "STANDARD";
    }

    @Override
    public String getDeliveryDescription() {
        return "Standard Delivery (5-7 business days) - $" + BASE_COST;
    }
}

