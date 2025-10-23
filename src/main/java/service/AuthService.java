package com.vehicle.spareparts.service;

import com.vehicle.spareparts.dto.*;
import com.vehicle.spareparts.entity.Role;
import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.RoleRepository;
import com.vehicle.spareparts.repository.UserRepository;
import com.vehicle.spareparts.security.JwtTokenProvider;
import com.vehicle.spareparts.factory.user.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserFactory userFactory;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        logger.info("Authenticating user: {}", request.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            logger.info("Authentication successful for user: {}", request.getUsername());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            logger.info("Returning auth response for user: {} with role: {}", user.getUsername(), user.getRole().getName());
            
            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole().getName()
            );
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}. Error: {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }
    
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        User user = userFactory.createUser(role.getName(), request);

        logger.info("Created new user via Factory: {} with role: {}", user.getUsername(), role.getName());
        logger.info("User Type Description: {}", userFactory.getUserTypeDescription(role.getName()));

        userRepository.save(user);
        
        return new MessageResponse("User registered successfully as " + role.getName());
    }
}
