package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer - Email Notification
 * Sends email notifications when order events occur
 */
@Component
public class EmailNotificationObserver implements OrderObserver {

    @Override
    public void update(Order order, String event) {
        String customerEmail = order.getCustomer().getEmail();
        String message = generateEmailMessage(order, event);

        // Simulate sending email
        System.out.println("📧 EMAIL NOTIFICATION");
        System.out.println("To: " + customerEmail);
        System.out.println("Subject: Order " + order.getOrderNumber() + " - " + event);
        System.out.println("Message: " + message);
        System.out.println("-----------------------------------");

        // In real implementation, integrate with email service (JavaMail, SendGrid, etc.)
    }

    private String generateEmailMessage(Order order, String event) {
        switch (event.toUpperCase()) {
            case "ORDER_CREATED":
                return "Your order " + order.getOrderNumber() + " has been created successfully. " +
                       "Total: $" + order.getTotalAmount();
            case "ORDER_APPROVED":
                return "Your order " + order.getOrderNumber() + " has been approved and will be processed soon.";
            case "ORDER_SHIPPED":
                return "Your order " + order.getOrderNumber() + " has been shipped!";
            case "ORDER_DELIVERED":
                return "Your order " + order.getOrderNumber() + " has been delivered. Thank you for your purchase!";
            case "ORDER_CANCELLED":
                return "Your order " + order.getOrderNumber() + " has been cancelled.";
            default:
                return "Order " + order.getOrderNumber() + " status updated: " + event;
        }
    }

    @Override
    public String getObserverName() {
        return "EMAIL_NOTIFICATION";
    }
}

