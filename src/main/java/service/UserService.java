package com.vehicle.spareparts.service;

import com.vehicle.spareparts.entity.Role;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.RoleRepository;
import com.vehicle.spareparts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    // Create new user
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set role if not already set
        if (user.getRole() == null || user.getRole().getId() == null) {
            // Default to CUSTOMER role if not specified
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.setRole(customerRole);
        } else {
            // Validate role exists
            Role role = roleRepository.findById(user.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }
        
        // Set active by default
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        
        return userRepository.save(user);
    }
    
    // Update user
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        
        // Check if new username is taken by another user
        if (!existingUser.getUsername().equals(userDetails.getUsername()) 
                && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }
        
        // Check if new email is taken by another user
        if (!existingUser.getEmail().equals(userDetails.getEmail()) 
                && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        
        // Update fields
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setIsActive(userDetails.getIsActive());
        
        // Update password only if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        // Update role if provided
        if (userDetails.getRole() != null && userDetails.getRole().getId() != null) {
            Role role = roleRepository.findById(userDetails.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existingUser.setRole(role);
        }
        
        return userRepository.save(existingUser);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    // Get users by role
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }
    
    // Get active users
    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }
    
    // Get inactive users
    public List<User> getInactiveUsers() {
        return userRepository.findByIsActive(false);
    }
    
    // Deactivate user (soft delete)
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        return userRepository.save(user);
    }
    
    // Activate user
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }
}
