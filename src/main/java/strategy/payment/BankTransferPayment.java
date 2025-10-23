package com.vehicle.spareparts.strategy.payment;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy - Bank Transfer Payment Implementation
 */
@Component
public class BankTransferPayment implements PaymentStrategy {

    @Override
    public String processPayment(Order order) {
        // Simulate bank transfer payment processing
        System.out.println("Processing Bank Transfer Payment for Order: " + order.getOrderNumber());
        System.out.println("Amount: $" + order.getTotalAmount());
        System.out.println("Please transfer to Account: 1234567890");

        // Bank transfer requires manual verification
        order.setPaymentStatus("PENDING_VERIFICATION");

        return "Bank Transfer initiated for order " + order.getOrderNumber() +
               ". Please transfer $" + order.getTotalAmount() + " to Account: 1234567890. " +
               "Payment pending verification.";
    }

    @Override
    public boolean validatePayment(Order order) {
        // Validate bank transfer payment details
        return order.getPaymentMethod() != null &&
               order.getPaymentMethod().equalsIgnoreCase("BANK_TRANSFER") &&
               order.getTotalAmount() != null;
    }

    @Override
    public String getPaymentMethodName() {
        return "BANK_TRANSFER";
    }
}

