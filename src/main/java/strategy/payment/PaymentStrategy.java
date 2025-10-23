package com.vehicle.spareparts.strategy.payment;

import com.vehicle.spareparts.entity.Order;

/**
 * Strategy Pattern - Payment Strategy Interface
 * Defines contract for different payment methods
 */
public interface PaymentStrategy {

    /**
     * Process payment for an order
     * @param order The order to process payment for
     * @return Payment confirmation message
     */
    String processPayment(Order order);

    /**
     * Validate payment details
     * @param order The order to validate
     * @return true if valid, false otherwise
     */
    boolean validatePayment(Order order);

    /**
     * Get payment method name
     * @return Payment method identifier
     */
    String getPaymentMethodName();
}

