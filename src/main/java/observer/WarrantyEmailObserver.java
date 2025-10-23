package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Warranty;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer - Warranty Email Notification
 * Sends email notifications for warranty events
 */
@Component
public class WarrantyEmailObserver implements WarrantyObserver {

    @Override
    public void update(Warranty warranty, String event) {
        String customerEmail = warranty.getCustomer().getEmail();
        String message = generateEmailMessage(warranty, event);

        // Simulate sending email
        System.out.println("📧 WARRANTY EMAIL NOTIFICATION");
        System.out.println("To: " + customerEmail);
        System.out.println("Subject: Warranty " + warranty.getWarrantyNumber() + " - " + event);
        System.out.println("Message: " + message);
        System.out.println("-----------------------------------");

        // In real implementation, integrate with email service
    }

    private String generateEmailMessage(Warranty warranty, String event) {
        switch (event.toUpperCase()) {
            case "WARRANTY_CREATED":
                return "Congratulations! Your warranty " + warranty.getWarrantyNumber() +
                       " has been created for " + warranty.getSparePart().getPartName() +
                       ". Valid until " + warranty.getExpiryDate() + ".";

            case "WARRANTY_CLAIM_FILED":
                return "Your warranty claim for " + warranty.getWarrantyNumber() +
                       " has been submitted. We will review it shortly.";

            case "WARRANTY_CLAIM_APPROVED":
                return "Great news! Your warranty claim " + warranty.getWarrantyNumber() +
                       " has been approved. A replacement will be sent to you.";

            case "WARRANTY_CLAIM_REJECTED":
                return "Your warranty claim " + warranty.getWarrantyNumber() +
                       " has been rejected. Reason: " + warranty.getClaimNotes();

            case "WARRANTY_EXPIRING":
                return "Your warranty " + warranty.getWarrantyNumber() +
                       " for " + warranty.getSparePart().getPartName() +
                       " is expiring on " + warranty.getExpiryDate() + ".";

            case "WARRANTY_EXPIRED":
                return "Your warranty " + warranty.getWarrantyNumber() +
                       " has expired. Consider purchasing an extended warranty.";

            default:
                return "Warranty " + warranty.getWarrantyNumber() + " - " + event;
        }
    }

    @Override
    public String getObserverName() {
        return "WARRANTY_EMAIL_NOTIFICATION";
    }
}

