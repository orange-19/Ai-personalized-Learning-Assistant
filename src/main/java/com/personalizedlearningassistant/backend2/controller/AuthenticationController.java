package com.personalizedlearningassistant.backend2.controller;

import com.personalizedlearningassistant.backend2.configuration.JwtUtility;
import com.personalizedlearningassistant.backend2.dto.AuthRequest;
import com.personalizedlearningassistant.backend2.dto.AuthResponse;
import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Login endpoint - authenticates user and returns JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            String token = jwtUtility.generateToken(authRequest.getUsername());

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", authRequest.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid username or password"));
        }
    }

    /**
     * Register endpoint - creates a new user account
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserProfile userProfile) {
        try {
            // Check if user already exists
            UserProfile existingUser = profileRepository.findByUsername(userProfile.getUsername());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Username already exists"));
            }

            // Encode password
            userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));

            // Save user
            profileRepository.save(userProfile);

            // Generate token
            String token = jwtUtility.generateToken(userProfile.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(token, "Registration successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Validate token endpoint
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtility.validateToken(token)) {
                String username = jwtUtility.extractUsername(token);
                return ResponseEntity.ok(new AuthResponse(token, "Token is valid. Username: " + username));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, "Token is invalid or expired"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Token validation failed: " + e.getMessage()));
        }
    }
}

