package com.personalizedlearningassistant.backend2.controller.profilecon;

import com.personalizedlearningassistant.backend2.configuration.JwtUtility;
import com.personalizedlearningassistant.backend2.dto.AuthResponse;
import com.personalizedlearningassistant.backend2.dto.profiledtos.Profiledto;
import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import com.personalizedlearningassistant.backend2.services.profile.ProfileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileServices profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JwtUtility jwtUtility;

    /**
     * Register/Create profile endpoint
     * Takes UserProfile data (username, password, name, rollno, email, avatarUrl)
     * Returns JWT token on successful registration
     */
    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Profiledto profiledto) {
        try {
            // Validate required fields
            if (profiledto.getUsername() == null || profiledto.getUsername().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Username is required"));
            }

            if (profiledto.getPassword() == null || profiledto.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Password is required"));
            }

            // Check if user already exists
            UserProfile existingUser = profileRepository.findByUsername(profiledto.getUsername());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, "Username already exists"));
            }

            // Create profile with encoded password
            profileService.createProfile(profiledto);

            // Generate JWT token
            String token = jwtUtility.generateToken(profiledto.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(token, "Registration successful"));

        } catch (Exception ex) {
            logger.error("Error during profile creation/registration", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "Registration failed: " + ex.getMessage()));
        }
    }

    @GetMapping("/get-profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        try {
            Profiledto profile = profileService.getProfile(username);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(404).body("Profile not found for username: " + username);
            }
        } catch (Exception ex) {
            logger.error("Error while fetching profile for username {}", username, ex);
            return ResponseEntity.status(500).body("An error occurred while fetching profile: " + ex.getMessage());
        }
    }

    // Support correct path and a common misspelling to prevent 404 for clients that use the typo
    @PutMapping(path = "/update-profile/{username}", consumes = "application/json")
    public ResponseEntity<?> updateProfilePut(@PathVariable String username, @RequestBody Profiledto profiledto) {
        return handleUpdate(username, profiledto);
    }

    @PatchMapping(path = "/update-profile/{username}", consumes = "application/json")
    public ResponseEntity<?> updateProfilePatch(@PathVariable String username, @RequestBody Profiledto profiledto) {
        return handleUpdate(username, profiledto);
    }

    // Alias for clients that typo the endpoint
    @RequestMapping(value = "/upate-profile/{username}", method = {RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.POST}, consumes = "application/json")
    public ResponseEntity<?> updateProfileTypo(@PathVariable String username, @RequestBody Profiledto profiledto) {
        logger.warn("Received request on misspelled path '/upate-profile' for username {}", username);
        return handleUpdate(username, profiledto);
    }

    // Centralized handler to keep behavior consistent and concise
    private ResponseEntity<?> handleUpdate(String username, Profiledto profiledto) {
        try {
            profileService.updateProfile(username, profiledto);
            return ResponseEntity.ok().body("Profile updated successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Update failed - profile not found: {}", username);
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error while updating profile for username {}", username, e);
            return ResponseEntity.status(500).body("An error occurred while updating profile: " + e.getMessage());
        }
    }
}
