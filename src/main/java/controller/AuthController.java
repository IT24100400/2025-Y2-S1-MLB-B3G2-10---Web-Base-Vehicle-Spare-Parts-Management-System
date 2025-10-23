package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.dto.AuthResponse;
import com.vehicle.spareparts.dto.LoginRequest;
import com.vehicle.spareparts.dto.MessageResponse;
import com.vehicle.spareparts.dto.RegisterRequest;
import com.vehicle.spareparts.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for user: {}", request.getUsername());
            AuthResponse response = authService.login(request);
            logger.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for user: {}. Error: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            MessageResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
