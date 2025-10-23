package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Warranty;

/**
 * Observer Pattern - Warranty Observer Interface
 * Defines contract for observers that react to warranty events
 */
public interface WarrantyObserver {

    /**
     * Update observer with warranty event
     * @param warranty The warranty that triggered the event
     * @param event The event type (CREATED, CLAIMED, APPROVED, REJECTED, EXPIRING)
     */
    void update(Warranty warranty, String event);

    /**
     * Get observer name/type
     * @return Observer identifier
     */
    String getObserverName();
}

