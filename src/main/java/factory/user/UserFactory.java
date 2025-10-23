package com.vehicle.spareparts.factory.user;

import com.vehicle.spareparts.dto.RegisterRequest;
import com.vehicle.spareparts.entity.Role;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern - User Factory
 * Creates different types of users based on role
 */
@Component
public class UserFactory {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create user based on role type
     */
    public User createUser(String roleType, RegisterRequest request) {
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(role);
        user.setIsActive(true);

        // Apply role-specific configurations
        switch (roleType.toUpperCase()) {
            case "ADMIN":
                return createAdmin(user);
            case "CUSTOMER":
                return createCustomer(user);
            case "DELIVERY_STAFF":
                return createDeliveryStaff(user);
            case "STORE_OWNER":
                return createStoreOwner(user);
            default:
                throw new IllegalArgumentException("Unknown role type: " + roleType);
        }
    }

    /**
     * Create Admin user with specific configurations
     */
    private User createAdmin(User user) {
        System.out.println("Creating ADMIN user: " + user.getUsername());
        // Admin-specific setup (full system access)
        user.setIsActive(true);
        return user;
    }

    /**
     * Create Customer user with specific configurations
     */
    private User createCustomer(User user) {
        System.out.println("Creating CUSTOMER user: " + user.getUsername());
        // Customer-specific setup (shopping privileges)
        user.setIsActive(true);
        return user;
    }

    /**
     * Create Delivery Staff user with specific configurations
     */
    private User createDeliveryStaff(User user) {
        System.out.println("Creating DELIVERY_STAFF user: " + user.getUsername());
        // Delivery staff-specific setup (delivery management access)
        user.setIsActive(true);
        return user;
    }

    /**
     * Create Store Owner user with specific configurations
     */
    private User createStoreOwner(User user) {
        System.out.println("Creating STORE_OWNER user: " + user.getUsername());
        // Store owner-specific setup (inventory and order management)
        user.setIsActive(true);
        return user;
    }

    /**
     * Get user type description
     */
    public String getUserTypeDescription(String roleType) {
        switch (roleType.toUpperCase()) {
            case "ADMIN":
                return "Administrator with full system access";
            case "CUSTOMER":
                return "Customer with shopping and order tracking privileges";
            case "DELIVERY_STAFF":
                return "Delivery personnel with delivery management access";
            case "STORE_OWNER":
                return "Store owner with inventory and order management access";
            default:
                return "Unknown user type";
        }
    }
}

