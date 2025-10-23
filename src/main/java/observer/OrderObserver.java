package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Order;

/**
 * Observer Pattern - Observer Interface
 * Defines contract for observers that react to order events
 */
public interface OrderObserver {

    /**
     * Update observer with order event
     * @param order The order that triggered the event
     * @param event The event type (status change, creation, etc.)
     */
    void update(Order order, String event);

    /**
     * Get observer name/type
     * @return Observer identifier
     */
    String getObserverName();
}

