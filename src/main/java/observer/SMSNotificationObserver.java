package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer - SMS Notification
 * Sends SMS notifications when order events occur
 */
@Component
public class SMSNotificationObserver implements OrderObserver {

    @Override
    public void update(Order order, String event) {
        String customerPhone = order.getCustomer().getPhone();

        if (customerPhone == null || customerPhone.isEmpty()) {
            System.out.println("⚠️ SMS not sent: Customer phone number not available");
            return;
        }

        String message = generateSMSMessage(order, event);

        // Simulate sending SMS
        System.out.println("📱 SMS NOTIFICATION");
        System.out.println("To: " + customerPhone);
        System.out.println("Message: " + message);
        System.out.println("-----------------------------------");

        // In real implementation, integrate with SMS service (Twilio, AWS SNS, etc.)
    }

    private String generateSMSMessage(Order order, String event) {
        switch (event.toUpperCase()) {
            case "ORDER_CREATED":
                return "Order " + order.getOrderNumber() + " created. Total: $" + order.getTotalAmount();
            case "ORDER_APPROVED":
                return "Order " + order.getOrderNumber() + " approved!";
            case "ORDER_SHIPPED":
                return "Order " + order.getOrderNumber() + " shipped! Track your delivery.";
            case "ORDER_DELIVERED":
                return "Order " + order.getOrderNumber() + " delivered. Thank you!";
            case "ORDER_CANCELLED":
                return "Order " + order.getOrderNumber() + " cancelled.";
            default:
                return "Order " + order.getOrderNumber() + " - " + event;
        }
    }

    @Override
    public String getObserverName() {
        return "SMS_NOTIFICATION";
    }
}

