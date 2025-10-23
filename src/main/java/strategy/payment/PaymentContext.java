package com.vehicle.spareparts.strategy.payment;

import com.vehicle.spareparts.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context Class for Payment Strategy Pattern
 * Manages payment strategy selection and execution
 */
@Component
public class PaymentContext {

    private final Map<String, PaymentStrategy> strategies = new HashMap<>();

    @Autowired
    public PaymentContext(List<PaymentStrategy> paymentStrategies) {
        // Auto-register all payment strategies
        for (PaymentStrategy strategy : paymentStrategies) {
            strategies.put(strategy.getPaymentMethodName().toUpperCase(), strategy);
        }
    }

    /**
     * Execute payment using appropriate strategy
     */
    public String executePayment(Order order) {
        PaymentStrategy strategy = getStrategy(order.getPaymentMethod());

        if (!strategy.validatePayment(order)) {
            throw new IllegalArgumentException("Invalid payment details for " + order.getPaymentMethod());
        }

        return strategy.processPayment(order);
    }

    /**
     * Get payment strategy by method name
     */
    public PaymentStrategy getStrategy(String paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        PaymentStrategy strategy = strategies.get(paymentMethod.toUpperCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        return strategy;
    }

    /**
     * Check if payment method is supported
     */
    public boolean isPaymentMethodSupported(String paymentMethod) {
        return strategies.containsKey(paymentMethod.toUpperCase());
    }
}

