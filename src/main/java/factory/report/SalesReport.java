package com.vehicle.spareparts.factory.report;

import com.vehicle.spareparts.entity.Order;
import com.vehicle.spareparts.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete Report - Sales Report Implementation
 */
public class SalesReport implements Report {

    private final OrderRepository orderRepository;
    private final LocalDateTime generatedAt;
    private Map<String, Object> reportData;

    public SalesReport(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.generatedAt = LocalDateTime.now();
    }

    @Override
    public Map<String, Object> generateReportData() {
        if (reportData == null) {
            reportData = new HashMap<>();

            List<Order> allOrders = orderRepository.findAll();

            // Calculate total sales
            BigDecimal totalSales = allOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Count orders by status
            long completedOrders = allOrders.stream()
                    .filter(o -> "DELIVERED".equals(o.getStatus()))
                    .count();

            long pendingOrders = allOrders.stream()
                    .filter(o -> "PENDING".equals(o.getStatus()))
                    .count();

            reportData.put("totalOrders", allOrders.size());
            reportData.put("totalSales", totalSales);
            reportData.put("completedOrders", completedOrders);
            reportData.put("pendingOrders", pendingOrders);
            reportData.put("averageOrderValue",
                    allOrders.isEmpty() ? BigDecimal.ZERO :
                    totalSales.divide(BigDecimal.valueOf(allOrders.size()), 2, BigDecimal.ROUND_HALF_UP));
        }

        return reportData;
    }

    @Override
    public String getReportTitle() {
        return "Sales Report";
    }

    @Override
    public String getReportType() {
        return "SALES";
    }

    @Override
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String exportToString() {
        Map<String, Object> data = generateReportData();
        StringBuilder sb = new StringBuilder();
        sb.append("=== SALES REPORT ===\n");
        sb.append("Generated: ").append(generatedAt).append("\n\n");
        sb.append("Total Orders: ").append(data.get("totalOrders")).append("\n");
        sb.append("Total Sales: $").append(data.get("totalSales")).append("\n");
        sb.append("Completed Orders: ").append(data.get("completedOrders")).append("\n");
        sb.append("Pending Orders: ").append(data.get("pendingOrders")).append("\n");
        sb.append("Average Order Value: $").append(data.get("averageOrderValue")).append("\n");
        return sb.toString();
    }
}

