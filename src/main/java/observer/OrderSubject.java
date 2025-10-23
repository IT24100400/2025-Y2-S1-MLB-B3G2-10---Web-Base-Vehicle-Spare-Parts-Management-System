package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject/Observable - Order Subject
 * Manages observers and notifies them of order events
 */
@Component
public class OrderSubject {

    private final List<OrderObserver> observers = new ArrayList<>();

    @Autowired
    public OrderSubject(List<OrderObserver> orderObservers) {
        // Auto-register all observers
        this.observers.addAll(orderObservers);
        System.out.println("✅ Registered " + observers.size() + " order observers:");
        observers.forEach(o -> System.out.println("   - " + o.getObserverName()));
    }

    /**
     * Attach an observer
     */
    public void attach(OrderObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observer attached: " + observer.getObserverName());
        }
    }

    /**
     * Detach an observer
     */
    public void detach(OrderObserver observer) {
        observers.remove(observer);
        System.out.println("Observer detached: " + observer.getObserverName());
    }

    /**
     * Notify all observers of an event
     */
    public void notifyObservers(Order order, String event) {
        System.out.println("\n🔔 NOTIFYING OBSERVERS: " + event);
        System.out.println("Order: " + order.getOrderNumber());
        System.out.println("===================================");

        for (OrderObserver observer : observers) {
            try {
                observer.update(order, event);
            } catch (Exception e) {
                System.err.println("Error notifying observer " + observer.getObserverName() + ": " + e.getMessage());
            }
        }

        System.out.println("✅ All observers notified\n");
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
        for (OrderObserver observer : observers) {
            names.add(observer.getObserverName());
        }
        return names;
    }
}

