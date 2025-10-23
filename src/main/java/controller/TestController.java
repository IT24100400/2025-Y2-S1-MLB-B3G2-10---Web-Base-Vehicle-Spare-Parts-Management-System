package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.entity.User;
import com.vehicle.spareparts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/check-password/{username}")
    public String checkPassword(@PathVariable String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "User not found: " + username;
        }
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        
        return String.format(
            "Username: %s%n" +
            "Password from DB (first 30 chars): %s...%n" +
            "Password matches: %s%n" +
            "User active: %s%n" +
            "Role: %s",
            username,
            user.getPassword().substring(0, Math.min(30, user.getPassword().length())),
            matches,
            user.getIsActive(),
            user.getRole().getName()
        );
    }
    
    @GetMapping("/generate-hash")
    public String generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        return String.format(
            "Plain text: %s%n" +
            "BCrypt hash: %s%n" +
            "Verification test: %s",
            password,
            hash,
            passwordEncoder.matches(password, hash)
        );
    }
}
