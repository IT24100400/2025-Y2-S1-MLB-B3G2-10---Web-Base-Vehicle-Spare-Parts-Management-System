package com.vehicle.spareparts.factory.report;

import com.vehicle.spareparts.entity.SparePart;
import com.vehicle.spareparts.repository.SparePartRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete Report - Inventory Report Implementation
 */
public class InventoryReport implements Report {

    private final SparePartRepository sparePartRepository;
    private final LocalDateTime generatedAt;
    private Map<String, Object> reportData;

    public InventoryReport(SparePartRepository sparePartRepository) {
        this.sparePartRepository = sparePartRepository;
        this.generatedAt = LocalDateTime.now();
    }

    @Override
    public Map<String, Object> generateReportData() {
        if (reportData == null) {
            reportData = new HashMap<>();

            List<SparePart> allParts = sparePartRepository.findAll();

            // Total parts count
            int totalParts = allParts.size();

            // Low stock items (stock < reorder level)
            List<SparePart> lowStockItems = allParts.stream()
                    .filter(p -> p.getStockQuantity() < p.getReorderLevel())
                    .collect(Collectors.toList());

            // Out of stock items
            long outOfStockCount = allParts.stream()
                    .filter(p -> p.getStockQuantity() == 0)
                    .count();

            // Total inventory value
            double totalInventoryValue = allParts.stream()
                    .mapToDouble(p -> p.getPrice().doubleValue() * p.getStockQuantity())
                    .sum();

            reportData.put("totalParts", totalParts);
            reportData.put("lowStockCount", lowStockItems.size());
            reportData.put("lowStockItems", lowStockItems);
            reportData.put("outOfStockCount", outOfStockCount);
            reportData.put("totalInventoryValue", totalInventoryValue);
        }

        return reportData;
    }

    @Override
    public String getReportTitle() {
        return "Inventory Report";
    }

    @Override
    public String getReportType() {
        return "INVENTORY";
    }

    @Override
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String exportToString() {
        Map<String, Object> data = generateReportData();
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY REPORT ===\n");
        sb.append("Generated: ").append(generatedAt).append("\n\n");
        sb.append("Total Parts: ").append(data.get("totalParts")).append("\n");
        sb.append("Low Stock Items: ").append(data.get("lowStockCount")).append("\n");
        sb.append("Out of Stock: ").append(data.get("outOfStockCount")).append("\n");
        sb.append("Total Inventory Value: $").append(String.format("%.2f", data.get("totalInventoryValue"))).append("\n");
        return sb.toString();
    }
}

