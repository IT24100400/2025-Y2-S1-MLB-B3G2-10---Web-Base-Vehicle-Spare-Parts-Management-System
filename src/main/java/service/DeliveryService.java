package com.vehicle.spareparts.service;

import com.vehicle.spareparts.entity.Delivery;
import com.vehicle.spareparts.entity.Order;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.DeliveryRepository;
import com.vehicle.spareparts.repository.OrderRepository;
import com.vehicle.spareparts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {
    
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Transactional(readOnly = true)
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
    }
    
    @Transactional(readOnly = true)
    public List<Delivery> getDeliveriesByStaff(String username) {
        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return deliveryRepository.findDeliveriesByStaff(staff.getId());
    }
    
    @Transactional(readOnly = true)
    public List<Delivery> getDeliveriesByStatus(String status) {
        return deliveryRepository.findByStatus(status);
    }
    
    @Transactional
    public Delivery updateDeliveryStatus(Long id, String status, String notes) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        delivery.setStatus(status);
        if (notes != null) {
            delivery.setTrackingNotes(notes);
        }
        
        if ("DISPATCHED".equals(status)) {
            delivery.setDispatchedAt(LocalDateTime.now());
        } else if ("DELIVERED".equals(status)) {
            delivery.setDeliveredAt(LocalDateTime.now());
            
            //  Update Order status to DELIVERED when delivery is completed
            Order order = delivery.getOrder();
            if (order != null && !"DELIVERED".equals(order.getStatus())) {
                order.setStatus("DELIVERED");
                order .setDeliveredAt(LocalDateTime.now());
                orderRepository.save(order);
            }
        }
        
        return deliveryRepository.save(delivery);
    }
    
    @Transactional
    public Delivery assignDeliveryStaff(Long id, Long staffId) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Delivery staff not found"));
        
        if (!"DELIVERY_STAFF".equals(staff.getRole().getName())) {
            throw new RuntimeException("User is not a delivery staff");
        }
        
        delivery.setDeliveryStaff(staff);
        delivery.setAssignedAt(LocalDateTime.now());
        
        return deliveryRepository.save(delivery);
    }
    
    @Transactional
    public Delivery updateDeliveryDetails(Long id, Long staffId, String address) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        // Update delivery staff if provided
        if (staffId != null) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Delivery staff not found"));
            
            if (!"DELIVERY_STAFF".equals(staff.getRole().getName())) {
                throw new RuntimeException("User is not a delivery staff");
            }
            
            delivery.setDeliveryStaff(staff);
            if (delivery.getAssignedAt() == null) {
                delivery.setAssignedAt(LocalDateTime.now());
            }
        }
        
        // Update delivery address if provided
        if (address != null && !address.trim().isEmpty()) {
            delivery.setDeliveryAddress(address);
        }
        
        return deliveryRepository.save(delivery);
    }
    
    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        // Only allow deletion of DELIVERED deliveries
        if (!"DELIVERED".equals(delivery.getStatus())) {
            throw new RuntimeException("Only completed (DELIVERED) deliveries can be deleted");
        }
        
        deliveryRepository.delete(delivery);
    }
}
