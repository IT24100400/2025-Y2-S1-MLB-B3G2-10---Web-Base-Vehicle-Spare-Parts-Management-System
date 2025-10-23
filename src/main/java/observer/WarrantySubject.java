package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Warranty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject/Observable - Warranty Subject
 * Manages warranty observers and notifies them of warranty events
 */
@Component
public class WarrantySubject {

    private final List<WarrantyObserver> observers = new ArrayList<>();

    @Autowired
    public WarrantySubject(List<WarrantyObserver> warrantyObservers) {
        // Auto-register all warranty observers
        this.observers.addAll(warrantyObservers);
        System.out.println("✅ Registered " + observers.size() + " warranty observers:");
        observers.forEach(o -> System.out.println("   - " + o.getObserverName()));
    }

    /**
     * Attach an observer
     */
    public void attach(WarrantyObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Warranty Observer attached: " + observer.getObserverName());
        }
    }

    /**
     * Detach an observer
     */
    public void detach(WarrantyObserver observer) {
        observers.remove(observer);
        System.out.println("Warranty Observer detached: " + observer.getObserverName());
    }

    /**
     * Notify all observers of a warranty event
     */
    public void notifyObservers(Warranty warranty, String event) {
        System.out.println("\n🔔 NOTIFYING WARRANTY OBSERVERS: " + event);
        System.out.println("Warranty: " + warranty.getWarrantyNumber());
        System.out.println("===================================");

        for (WarrantyObserver observer : observers) {
            try {
                observer.update(warranty, event);
            } catch (Exception e) {
                System.err.println("Error notifying warranty observer " +
                                 observer.getObserverName() + ": " + e.getMessage());
            }
        }

        System.out.println("✅ All warranty observers notified\n");
    }

    /**
     * Get count of registered observers
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Get list of observer names
     */
    public List<String> getObserverNames() {
        List<String> names = new ArrayList<>();
        for (WarrantyObserver observer : observers) {
            names.add(observer.getObserverName());
        }
        return names;
    }
}

