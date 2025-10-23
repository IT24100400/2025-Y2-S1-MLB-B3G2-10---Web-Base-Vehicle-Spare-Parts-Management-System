package com.vehicle.spareparts.factory.report;

import com.vehicle.spareparts.entity.Delivery;
import com.vehicle.spareparts.repository.DeliveryRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete Report - Delivery Report Implementation
 */
public class DeliveryReport implements Report {

    private final DeliveryRepository deliveryRepository;
    private final LocalDateTime generatedAt;
    private Map<String, Object> reportData;

    public DeliveryReport(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
        this.generatedAt = LocalDateTime.now();
    }

    @Override
    public Map<String, Object> generateReportData() {
        if (reportData == null) {
            reportData = new HashMap<>();

            List<Delivery> allDeliveries = deliveryRepository.findAll();

            // Count by status
            long pendingDeliveries = allDeliveries.stream()
                    .filter(d -> "PENDING".equals(d.getStatus()))
                    .count();

            long inTransitDeliveries = allDeliveries.stream()
                    .filter(d -> "IN_TRANSIT".equals(d.getStatus()))
                    .count();

            long deliveredCount = allDeliveries.stream()
                    .filter(d -> "DELIVERED".equals(d.getStatus()))
                    .count();

            long failedDeliveries = allDeliveries.stream()
                    .filter(d -> "FAILED".equals(d.getStatus()))
                    .count();

            reportData.put("totalDeliveries", allDeliveries.size());
            reportData.put("pendingDeliveries", pendingDeliveries);
            reportData.put("inTransitDeliveries", inTransitDeliveries);
            reportData.put("deliveredCount", deliveredCount);
            reportData.put("failedDeliveries", failedDeliveries);
            reportData.put("successRate",
                    allDeliveries.isEmpty() ? 0.0 :
                    (deliveredCount * 100.0 / allDeliveries.size()));
        }

        return reportData;
    }

    @Override
    public String getReportTitle() {
        return "Delivery Report";
    }

    @Override
    public String getReportType() {
        return "DELIVERY";
    }

    @Override
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String exportToString() {
        Map<String, Object> data = generateReportData();
        StringBuilder sb = new StringBuilder();
        sb.append("=== DELIVERY REPORT ===\n");
        sb.append("Generated: ").append(generatedAt).append("\n\n");
        sb.append("Total Deliveries: ").append(data.get("totalDeliveries")).append("\n");
        sb.append("Pending: ").append(data.get("pendingDeliveries")).append("\n");
        sb.append("In Transit: ").append(data.get("inTransitDeliveries")).append("\n");
        sb.append("Delivered: ").append(data.get("deliveredCount")).append("\n");
        sb.append("Failed: ").append(data.get("failedDeliveries")).append("\n");
        sb.append("Success Rate: ").append(String.format("%.2f%%", data.get("successRate"))).append("\n");
        return sb.toString();
    }
}

