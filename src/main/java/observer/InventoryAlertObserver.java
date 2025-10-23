package com.vehicle.spareparts.observer;

import com.vehicle.spareparts.entity.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer - Inventory Alert
 * Monitors and alerts on low stock levels after order
 */
@Component
public class InventoryAlertObserver implements OrderObserver {

    @Override
    public void update(Order order, String event) {
        // Check inventory levels when order is created
        if ("ORDER_CREATED".equals(event)) {
            order.getOrderItems().forEach(item -> {
                int remainingStock = item.getSparePart().getStockQuantity();
                int reorderLevel = item.getSparePart().getReorderLevel();

                if (remainingStock <= reorderLevel) {
                    System.out.println("⚠️ INVENTORY ALERT");
                    System.out.println("Part: " + item.getSparePart().getPartName());
                    System.out.println("Current Stock: " + remainingStock);
                    System.out.println("Reorder Level: " + reorderLevel);
                    System.out.println("ACTION REQUIRED: Restock this item!");
                    System.out.println("-----------------------------------");
                }
            });
        }
    }

    @Override
    public String getObserverName() {
        return "INVENTORY_ALERT";
    }
}

