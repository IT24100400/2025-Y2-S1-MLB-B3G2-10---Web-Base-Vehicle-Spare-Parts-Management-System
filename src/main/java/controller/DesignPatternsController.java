package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.factory.report.Report;
import com.vehicle.spareparts.factory.report.ReportFactory;
import com.vehicle.spareparts.strategy.delivery.DeliveryContext;
import com.vehicle.spareparts.strategy.payment.PaymentContext;
import com.vehicle.spareparts.observer.OrderSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Design Patterns Controller
 * Demonstrates all implemented design patterns
 */
@RestController
@RequestMapping("/api/design-patterns")
@CrossOrigin(origins = "*")
public class DesignPatternsController {

    @Autowired
    private ReportFactory reportFactory;

    @Autowired
    private PaymentContext paymentContext;

    @Autowired
    private DeliveryContext deliveryContext;

    @Autowired
    private OrderSubject orderSubject;

    /**
     * FACTORY PATTERN - Generate Report
     * Endpoint: GET /api/design-patterns/reports/{type}
     * Example: /api/design-patterns/reports/SALES
     */
    @GetMapping("/reports/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> generateReport(@PathVariable String type) {
        try {
            // FACTORY PATTERN - Create report based on type
            Report report = reportFactory.createReport(type);

            Map<String, Object> response = new HashMap<>();
            response.put("reportType", report.getReportType());
            response.put("reportTitle", report.getReportTitle());
            response.put("generatedAt", report.getGeneratedAt());
            response.put("data", report.generateReportData());
            response.put("formattedReport", report.exportToString());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "availableTypes", reportFactory.getAvailableReportTypes()
            ));
        }
    }

    /**
     * FACTORY PATTERN - Get Available Report Types
     * Endpoint: GET /api/design-patterns/reports/available
     */
    @GetMapping("/reports/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> getAvailableReports() {
        Map<String, String> reports = new HashMap<>();
        for (String type : reportFactory.getAvailableReportTypes()) {
            reports.put(type, reportFactory.getReportTypeDescription(type));
        }
        return ResponseEntity.ok(reports);
    }

    /**
     * STRATEGY PATTERN - Get Available Payment Methods
     * Endpoint: GET /api/design-patterns/payment-methods
     */
    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
        Map<String, Object> response = new HashMap<>();
        response.put("availableMethods", new String[]{
            "CREDIT_CARD",
            "BANK_TRANSFER",
            "CASH_ON_DELIVERY"
        });
        response.put("description", "Available payment strategies in the system");
        return ResponseEntity.ok(response);
    }

    /**
     * STRATEGY PATTERN - Get Available Delivery Methods
     * Endpoint: GET /api/design-patterns/delivery-methods
     */
    @GetMapping("/delivery-methods")
    public ResponseEntity<?> getDeliveryMethods() {
        Map<String, String> methods = deliveryContext.getAvailableDeliveryMethods();
        return ResponseEntity.ok(Map.of(
            "availableMethods", methods,
            "description", "Available delivery strategies in the system"
        ));
    }

    /**
     * OBSERVER PATTERN - Get Observer Status
     * Endpoint: GET /api/design-patterns/observers
     */
    @GetMapping("/observers")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> getObserverStatus() {
        return ResponseEntity.ok(Map.of(
            "observerCount", orderSubject.getObserverCount(),
            "registeredObservers", orderSubject.getObserverNames(),
            "description", "Active observers monitoring order events"
        ));
    }

    /**
     * Design Patterns Overview
     * Endpoint: GET /api/design-patterns/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getDesignPatternsOverview() {
        Map<String, Object> overview = new HashMap<>();

        // Strategy Pattern Info
        Map<String, Object> strategyPattern = new HashMap<>();
        strategyPattern.put("name", "Strategy Pattern");
        strategyPattern.put("usage", "Payment Processing & Delivery Management");
        strategyPattern.put("location", "com.vehicle.spareparts.strategy");
        strategyPattern.put("implementations", Map.of(
            "payment", new String[]{"CreditCardPayment", "BankTransferPayment", "CashOnDeliveryPayment"},
            "delivery", new String[]{"StandardDelivery", "ExpressDelivery", "PickupDelivery"}
        ));
        overview.put("strategyPattern", strategyPattern);

        // Factory Pattern Info
        Map<String, Object> factoryPattern = new HashMap<>();
        factoryPattern.put("name", "Factory Pattern");
        factoryPattern.put("usage", "User Creation & Report Generation");
        factoryPattern.put("location", "com.vehicle.spareparts.factory");
        factoryPattern.put("implementations", Map.of(
            "userFactory", new String[]{"ADMIN", "CUSTOMER", "DELIVERY_STAFF", "STORE_OWNER"},
            "reportFactory", new String[]{"SALES", "INVENTORY", "DELIVERY"}
        ));
        overview.put("factoryPattern", factoryPattern);

        // Observer Pattern Info
        Map<String, Object> observerPattern = new HashMap<>();
        observerPattern.put("name", "Observer Pattern");
        observerPattern.put("usage", "Order Event Notifications");
        observerPattern.put("location", "com.vehicle.spareparts.observer");
        observerPattern.put("implementations", new String[]{
            "EmailNotificationObserver",
            "SMSNotificationObserver",
            "AuditLogObserver",
            "InventoryAlertObserver"
        });
        observerPattern.put("activeObservers", orderSubject.getObserverCount());
        overview.put("observerPattern", observerPattern);

        // Singleton Pattern Info (Spring-managed)
        Map<String, Object> singletonPattern = new HashMap<>();
        singletonPattern.put("name", "Singleton Pattern");
        singletonPattern.put("usage", "Spring Framework Auto-Managed Singletons");
        singletonPattern.put("description", "All @Service, @Component, @Repository beans are singleton by default");
        singletonPattern.put("examples", new String[]{
            "OrderService",
            "PaymentContext",
            "DeliveryContext",
            "OrderSubject",
            "UserFactory",
            "ReportFactory"
        });
        overview.put("singletonPattern", singletonPattern);

        return ResponseEntity.ok(overview);
    }
}

