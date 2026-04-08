package com.personalizedlearningassistant.backend2.controller;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ProfileController - handles authenticated profile operations
 * Keep only authenticated profile operations:
 * - GET /api/profile/me — return current user's profile (extract from SecurityContext)
 * - PUT /api/profile/update — update profile fields
 * Remove ALL registration/auth logic from here
 */
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    /**
     * Get current user's profile
     * GET /api/profile/me
     * Extracts username from SecurityContext and returns user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User not authenticated", 401));
            }

            String username = authentication.getName();
            UserProfile userProfile = profileRepository.findByUsername(username);

            if (userProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Profile not found", 404));
            }

            return ResponseEntity.ok(userProfile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching profile: " + e.getMessage(), 500));
        }
    }

    /**
     * Update current user's profile
     * PUT /api/profile/update
     * Updates profile fields for authenticated user
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfile profileUpdates) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User not authenticated", 401));
            }

            String username = authentication.getName();
            UserProfile userProfile = profileRepository.findByUsername(username);

            if (userProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Profile not found", 404));
            }

            // Update allowed fields
            if (profileUpdates.getName() != null) {
                userProfile.setName(profileUpdates.getName());
            }
            if (profileUpdates.getEmail() != null) {
                userProfile.setEmail(profileUpdates.getEmail());
            }
            if (profileUpdates.getRollno() != null) {
                userProfile.setRollno(profileUpdates.getRollno());
            }
            if (profileUpdates.getAvatarUrl() != null) {
                userProfile.setAvatarUrl(profileUpdates.getAvatarUrl());
            }

            UserProfile updatedProfile = profileRepository.save(userProfile);

            return ResponseEntity.ok(updatedProfile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error updating profile: " + e.getMessage(), 500));
        }
    }

    /**
     * Get user profile by ID (for admin or specific use cases)
     * GET /api/profile/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
        try {
            UserProfile userProfile = profileRepository.findById(id).orElse(null);

            if (userProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Profile not found", 404));
            }

            return ResponseEntity.ok(userProfile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching profile: " + e.getMessage(), 500));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message, int status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", status);
        return errorResponse;
    }
}

