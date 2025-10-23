package com.vehicle.spareparts.strategy.delivery;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Concrete Strategy - Pickup Delivery Implementation
 */
@Component
public class PickupDelivery implements DeliveryStrategy {

    private static final BigDecimal BASE_COST = BigDecimal.ZERO;
    private static final int PICKUP_DAYS = 1;

    @Override
    public BigDecimal calculateDeliveryCost(Order order) {
        // No delivery cost for pickup
        System.out.println("Customer Pickup selected for Order: " + order.getOrderNumber());
        return BASE_COST;
    }

    @Override
    public LocalDateTime getEstimatedDeliveryTime() {
        // Ready for pickup in 1 day
        return LocalDateTime.now().plusDays(PICKUP_DAYS);
    }

    @Override
    public String getDeliveryMethodName() {
        return "PICKUP";
    }

    @Override
    public String getDeliveryDescription() {
        return "Store Pickup (Ready in 1 day) - Free";
    }
}

