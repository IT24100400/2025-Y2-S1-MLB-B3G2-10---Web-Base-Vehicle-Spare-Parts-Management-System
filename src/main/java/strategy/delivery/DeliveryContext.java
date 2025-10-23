package com.vehicle.spareparts.strategy.delivery;

import com.vehicle.spareparts.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context Class for Delivery Strategy Pattern
 * Manages delivery strategy selection and execution
 */
@Component
public class DeliveryContext {

    private final Map<String, DeliveryStrategy> strategies = new HashMap<>();

    @Autowired
    public DeliveryContext(List<DeliveryStrategy> deliveryStrategies) {
        // Auto-register all delivery strategies
        for (DeliveryStrategy strategy : deliveryStrategies) {
            strategies.put(strategy.getDeliveryMethodName().toUpperCase(), strategy);
        }
    }

    /**
     * Calculate delivery cost using appropriate strategy
     */
    public BigDecimal calculateDeliveryCost(Order order, String deliveryMethod) {
        DeliveryStrategy strategy = getStrategy(deliveryMethod);
        return strategy.calculateDeliveryCost(order);
    }

    /**
     * Get estimated delivery time
     */
    public LocalDateTime getEstimatedDeliveryTime(String deliveryMethod) {
        DeliveryStrategy strategy = getStrategy(deliveryMethod);
        return strategy.getEstimatedDeliveryTime();
    }

    /**
     * Get delivery strategy by method name
     */
    public DeliveryStrategy getStrategy(String deliveryMethod) {
        if (deliveryMethod == null) {
            deliveryMethod = "STANDARD"; // Default to standard delivery
        }

        DeliveryStrategy strategy = strategies.get(deliveryMethod.toUpperCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported delivery method: " + deliveryMethod);
        }

        return strategy;
    }

    /**
     * Get all available delivery methods
     */
    public Map<String, String> getAvailableDeliveryMethods() {
        Map<String, String> methods = new HashMap<>();
        for (DeliveryStrategy strategy : strategies.values()) {
            methods.put(strategy.getDeliveryMethodName(), strategy.getDeliveryDescription());
        }
        return methods;
    }
}

