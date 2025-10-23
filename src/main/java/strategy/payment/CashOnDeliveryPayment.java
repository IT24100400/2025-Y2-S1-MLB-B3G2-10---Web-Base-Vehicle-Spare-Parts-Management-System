package com.vehicle.spareparts.strategy.payment;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy - Cash on Delivery Payment Implementation
 */
@Component
public class CashOnDeliveryPayment implements PaymentStrategy {

    @Override
    public String processPayment(Order order) {
        // Simulate COD payment processing
        System.out.println("Processing Cash on Delivery for Order: " + order.getOrderNumber());
        System.out.println("Amount to collect: $" + order.getTotalAmount());

        // COD payment collected upon delivery
        order.setPaymentStatus("PENDING_COLLECTION");

        return "Cash on Delivery confirmed for order " + order.getOrderNumber() +
               ". Amount to be collected: $" + order.getTotalAmount() + " upon delivery.";
    }

    @Override
    public boolean validatePayment(Order order) {
        // Validate COD payment details
        return order.getPaymentMethod() != null &&
               order.getPaymentMethod().equalsIgnoreCase("CASH_ON_DELIVERY") &&
               order.getTotalAmount() != null &&
               order.getShippingAddress() != null;
    }

    @Override
    public String getPaymentMethodName() {
        return "CASH_ON_DELIVERY";
    }
}

