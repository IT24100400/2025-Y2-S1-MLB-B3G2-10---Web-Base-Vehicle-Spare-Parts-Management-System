package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Warranty;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer - Warranty SMS Notification
 * Sends SMS notifications for warranty events
 */
@Component
public class WarrantySMSObserver implements WarrantyObserver {

    @Override
    public void update(Warranty warranty, String event) {
        String customerPhone = warranty.getCustomer().getPhone();

        if (customerPhone == null || customerPhone.isEmpty()) {
            System.out.println("⚠️ WARRANTY SMS not sent: Customer phone number not available");
            return;
        }

        String message = generateSMSMessage(warranty, event);

        // Simulate sending SMS
        System.out.println("📱 WARRANTY SMS NOTIFICATION");
        System.out.println("To: " + customerPhone);
        System.out.println("Message: " + message);
        System.out.println("-----------------------------------");

        // In real implementation, integrate with SMS service (Twilio, AWS SNS)
    }

    private String generateSMSMessage(Warranty warranty, String event) {
        switch (event.toUpperCase()) {
            case "WARRANTY_CREATED":
                return "Warranty " + warranty.getWarrantyNumber() + " created for " +
                       warranty.getSparePart().getPartName() + ". Valid until " + warranty.getExpiryDate();

            case "WARRANTY_CLAIM_FILED":
                return "Warranty claim " + warranty.getWarrantyNumber() + " filed. Under review.";

            case "WARRANTY_CLAIM_APPROVED":
                return "Warranty claim " + warranty.getWarrantyNumber() + " APPROVED! Replacement coming.";

            case "WARRANTY_CLAIM_REJECTED":
                return "Warranty claim " + warranty.getWarrantyNumber() + " rejected. Check email for details.";

            case "WARRANTY_EXPIRING":
                return "Warranty " + warranty.getWarrantyNumber() + " expires on " + warranty.getExpiryDate();

            default:
                return "Warranty " + warranty.getWarrantyNumber() + " - " + event;
        }
    }

    @Override
    public String getObserverName() {
        return "WARRANTY_SMS_NOTIFICATION";
    }
}

