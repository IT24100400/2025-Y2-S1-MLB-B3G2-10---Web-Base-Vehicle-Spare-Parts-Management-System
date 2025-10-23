package com.vehicle.spareparts.strategy.delivery;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Concrete Strategy - Express Delivery Implementation
 */
@Component
public class ExpressDelivery implements DeliveryStrategy {

    private static final BigDecimal BASE_COST = new BigDecimal("25.00");
    private static final int DELIVERY_DAYS = 2;

    @Override
    public BigDecimal calculateDeliveryCost(Order order) {
        // Express delivery - higher cost
        System.out.println("Calculating Express Delivery cost for Order: " + order.getOrderNumber());

        // Additional charge for orders over $500
        BigDecimal cost = BASE_COST;
        if (order.getTotalAmount().compareTo(new BigDecimal("500")) > 0) {
            cost = cost.add(new BigDecimal("10.00"));
        }

        return cost;
    }

    @Override
    public LocalDateTime getEstimatedDeliveryTime() {
        // 1-2 business days
        return LocalDateTime.now().plusDays(DELIVERY_DAYS);
    }

    @Override
    public String getDeliveryMethodName() {
        return "EXPRESS";
    }

    @Override
    public String getDeliveryDescription() {
        return "Express Delivery (1-2 business days) - $" + BASE_COST + "+";
    }
}

