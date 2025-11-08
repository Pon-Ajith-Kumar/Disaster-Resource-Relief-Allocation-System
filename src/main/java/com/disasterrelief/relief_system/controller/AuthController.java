package com.disasterrelief.relief_system.controller;

import com.disasterrelief.relief_system.dto.ApiResponse;
import com.disasterrelief.relief_system.dto.AuthResponse;
import com.disasterrelief.relief_system.dto.LoginRequest;
import com.disasterrelief.relief_system.dto.RegisterRequest;
import com.disasterrelief.relief_system.model.User;
import com.disasterrelief.relief_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);

            // In a real application, you would generate a JWT token here
            AuthResponse response = new AuthResponse(
                    "dummy-jwt-token-" + user.getId(), // Replace with actual JWT
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(new ApiResponse(true, "Registration successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            // In a real application, you would generate a JWT token here
            AuthResponse response = new AuthResponse(
                    "dummy-jwt-token-" + user.getId(), // Replace with actual JWT
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        // Extract user ID from token (simplified for demo)
        // In production, implement proper JWT validation
        try {
            String userId = token.replace("Bearer dummy-jwt-token-", "");
            User user = userService.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(new ApiResponse(true, "User found", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid token"));
        }
    }
}