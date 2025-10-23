package com.vehicle.spareparts.strategy.payment;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy - Credit Card Payment Implementation
 */
@Component
public class CreditCardPayment implements PaymentStrategy {

    @Override
    public String processPayment(Order order) {
        // Simulate credit card payment processing
        System.out.println("Processing Credit Card Payment for Order: " + order.getOrderNumber());
        System.out.println("Amount: $" + order.getTotalAmount());

        // In real implementation, integrate with payment gateway
        order.setPaymentStatus("PAID");

        return "Credit Card payment processed successfully for order " + order.getOrderNumber() +
               ". Amount: $" + order.getTotalAmount();
    }

    @Override
    public boolean validatePayment(Order order) {
        // Validate credit card payment details
        return order.getPaymentMethod() != null &&
               order.getPaymentMethod().equalsIgnoreCase("CREDIT_CARD") &&
               order.getTotalAmount() != null;
    }

    @Override
    public String getPaymentMethodName() {
        return "CREDIT_CARD";
    }
}

