package com.vehicle.spareparts.service;

import com.vehicle.spareparts.dto.*;
import com.vehicle.spareparts.entity.*;
import com.vehicle.spareparts.repository.*;
import com.vehicle.spareparts.strategy.payment.PaymentContext;
import com.vehicle.spareparts.strategy.delivery.DeliveryContext;
import com.vehicle.spareparts.observer.OrderSubject;
import com.vehicle.spareparts.observer.WarrantySubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private SparePartRepository sparePartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private WarrantyRepository warrantyRepository;

    // STRATEGY PATTERN - Payment Processing
    @Autowired
    private PaymentContext paymentContext;

    // STRATEGY PATTERN - Delivery Management
    @Autowired
    private DeliveryContext deliveryContext;

    // OBSERVER PATTERN - Order Notifications
    @Autowired
    private OrderSubject orderSubject;

    // OBSERVER PATTERN - Warranty Notifications
    @Autowired
    private WarrantySubject warrantySubject;

    @Transactional
    public OrderResponse createOrder(OrderRequest request, String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Order order = new Order();
        order.setOrderNumber("ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            SparePart sparePart = sparePartRepository.findById(itemRequest.getSparePartId())
                    .orElseThrow(() -> new RuntimeException("Spare part not found: " + itemRequest.getSparePartId()));
            
            if (sparePart.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + sparePart.getPartName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSparePart(sparePart);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(sparePart.getPrice());
            orderItem.setSubtotal(sparePart.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItem.setWarrantyMonths(sparePart.getWarrantyMonths());
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
            
            // Update stock
            sparePart.setStockQuantity(sparePart.getStockQuantity() - itemRequest.getQuantity());
            sparePartRepository.save(sparePart);
        }
        
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);

        // STRATEGY PATTERN - Process Payment
        try {
            String paymentResult = paymentContext.executePayment(savedOrder);
            System.out.println("Payment Result: " + paymentResult);
        } catch (Exception e) {
            System.err.println("Payment processing error: " + e.getMessage());
        }

        // OBSERVER PATTERN - Notify all observers about new order
        orderSubject.notifyObservers(savedOrder, "ORDER_CREATED");

        return convertToResponse(savedOrder);
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToResponse(order);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return orderRepository.findCustomerOrders(customer.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findCustomerOrders(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderResponse approveOrder(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        User storeOwner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        order.setStatus("APPROVED");
        order.setApprovedBy(storeOwner);
        order.setApprovedAt(LocalDateTime.now());
        
        Order updated = orderRepository.save(order);
        
        // Create delivery
        createDelivery(updated);
        
        // OBSERVER PATTERN - Notify observers about order approval
        orderSubject.notifyObservers(updated, "ORDER_APPROVED");

        return convertToResponse(updated);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        String oldStatus = order.getStatus();
        order.setStatus(status);
        Order updated = orderRepository.save(order);

        // AUTOMATIC WARRANTY CREATION - When order is delivered
        if ("DELIVERED".equals(status) && !"DELIVERED".equals(oldStatus)) {
            createWarrantiesForOrder(updated);
        }

        // OBSERVER PATTERN - Notify observers about status change
        String event = "ORDER_" + status.toUpperCase();
        orderSubject.notifyObservers(updated, event);

        return convertToResponse(updated);
    }
    
    /**
     * Automatically create warranties for all items in a delivered order
     */
    private void createWarrantiesForOrder(Order order) {
        System.out.println("\n🛡️ CREATING WARRANTIES FOR ORDER: " + order.getOrderNumber());
        System.out.println("=================================================");

        for (OrderItem item : order.getOrderItems()) {
            // Only create warranty if part has warranty months
            if (item.getWarrantyMonths() != null && item.getWarrantyMonths() > 0) {
                Warranty warranty = new Warranty();

                // Generate unique warranty number
                warranty.setWarrantyNumber("WRN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                warranty.setOrderItem(item);
                warranty.setCustomer(order.getCustomer());
                warranty.setSparePart(item.getSparePart());
                warranty.setPurchaseDate(LocalDate.now());
                warranty.setExpiryDate(LocalDate.now().plusMonths(item.getWarrantyMonths()));
                warranty.setStatus("ACTIVE");

                Warranty savedWarranty = warrantyRepository.save(warranty);

                System.out.println("✅ WARRANTY CREATED:");
                System.out.println("   Number: " + warranty.getWarrantyNumber());
                System.out.println("   Part: " + item.getSparePart().getPartName());
                System.out.println("   Period: " + item.getWarrantyMonths() + " months");
                System.out.println("   Expires: " + warranty.getExpiryDate());
                System.out.println("-------------------------------------------------");

                // OBSERVER PATTERN - Notify customers about new warranty
                warrantySubject.notifyObservers(savedWarranty, "WARRANTY_CREATED");
            }
        }

        System.out.println("=================================================\n");
    }

    private void createDelivery(Order order) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryNumber("DEL" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        delivery.setOrder(order);
        delivery.setDeliveryAddress(order.getShippingAddress());
        delivery.setStatus("PENDING");

        // STRATEGY PATTERN - Calculate delivery cost based on delivery method
        String deliveryMethod = order.getNotes() != null && order.getNotes().contains("EXPRESS") ? "EXPRESS" : "STANDARD";
        BigDecimal deliveryCost = deliveryContext.calculateDeliveryCost(order, deliveryMethod);
        LocalDateTime estimatedDelivery = deliveryContext.getEstimatedDeliveryTime(deliveryMethod);

        System.out.println("Delivery Method: " + deliveryMethod);
        System.out.println("Delivery Cost: $" + deliveryCost);
        System.out.println("Estimated Delivery: " + estimatedDelivery);

        deliveryRepository.save(delivery);
    }
    
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getFullName());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setNotes(order.getNotes());
        
        if (order.getApprovedBy() != null) {
            response.setApprovedBy(order.getApprovedBy().getId());
            response.setApprovedByName(order.getApprovedBy().getFullName());
        }
        response.setApprovedAt(order.getApprovedAt());
        
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());
        response.setItems(items);
        
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        return response;
    }
    
    private OrderItemResponse convertItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setSparePartId(item.getSparePart().getId());
        response.setPartNumber(item.getSparePart().getPartNumber());
        response.setPartName(item.getSparePart().getPartName());
        response.setImageUrl(item.getSparePart().getImageUrl());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());
        response.setWarrantyMonths(item.getWarrantyMonths());
        return response;
    }
}
