package com.vehicle.spareparts.factory.report;

import com.vehicle.spareparts.repository.DeliveryRepository;
import com.vehicle.spareparts.repository.OrderRepository;
import com.vehicle.spareparts.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern - Report Factory
 * Creates different types of reports based on report type
 */
@Component
public class ReportFactory {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    /**
     * Create report based on report type
     */
    public Report createReport(String reportType) {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        switch (reportType.toUpperCase()) {
            case "SALES":
                return createSalesReport();
            case "INVENTORY":
                return createInventoryReport();
            case "DELIVERY":
                return createDeliveryReport();
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
    }

    /**
     * Create Sales Report
     */
    private Report createSalesReport() {
        System.out.println("Creating Sales Report...");
        return new SalesReport(orderRepository);
    }

    /**
     * Create Inventory Report
     */
    private Report createInventoryReport() {
        System.out.println("Creating Inventory Report...");
        return new InventoryReport(sparePartRepository);
    }

    /**
     * Create Delivery Report
     */
    private Report createDeliveryReport() {
        System.out.println("Creating Delivery Report...");
        return new DeliveryReport(deliveryRepository);
    }

    /**
     * Get available report types
     */
    public String[] getAvailableReportTypes() {
        return new String[]{"SALES", "INVENTORY", "DELIVERY"};
    }

    /**
     * Get report type description
     */
    public String getReportTypeDescription(String reportType) {
        switch (reportType.toUpperCase()) {
            case "SALES":
                return "Sales Report - Overview of total sales, orders, and revenue";
            case "INVENTORY":
                return "Inventory Report - Stock levels, low stock alerts, and inventory value";
            case "DELIVERY":
                return "Delivery Report - Delivery status, success rates, and logistics overview";
            default:
                return "Unknown report type";
        }
    }
}

