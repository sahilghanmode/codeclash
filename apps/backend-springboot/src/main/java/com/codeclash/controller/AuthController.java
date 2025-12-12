package com.codeclash.controller;

import com.codeclash.model.SignupRequest;
import com.codeclash.model.User;
import com.codeclash.repository.UserRepository;
import com.codeclash.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService; // Configured but unused - matches original Express backend behavior

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if user already exists
            if (userRepository.existsByEmail(signupRequest.getEmail())) {
                response.put("success", false);
                response.put("message", "User already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Create new user
            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(signupRequest.getPassword()); // Note: Storing in plain text like original

            User savedUser = userRepository.save(user);

            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "username", savedUser.getUsername(),
                "email", savedUser.getEmail()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Spring Boot with CodeClash");
        return ResponseEntity.ok(response);
    }
}