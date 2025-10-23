package com.vehicle.spareparts.strategy.delivery;

import com.vehicle.spareparts.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy Pattern - Delivery Strategy Interface
 * Defines contract for different delivery methods
 */
public interface DeliveryStrategy {

    /**
     * Calculate delivery cost
     * @param order The order to calculate delivery for
     * @return Delivery cost
     */
    BigDecimal calculateDeliveryCost(Order order);

    /**
     * Get estimated delivery time
     * @return Estimated delivery date and time
     */
    LocalDateTime getEstimatedDeliveryTime();

    /**
     * Get delivery method name
     * @return Delivery method identifier
     */
    String getDeliveryMethodName();

    /**
     * Get delivery description
     * @return Description of delivery method
     */
    String getDeliveryDescription();
}

