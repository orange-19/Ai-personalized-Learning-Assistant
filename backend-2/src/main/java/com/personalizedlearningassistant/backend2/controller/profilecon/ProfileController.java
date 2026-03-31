package com.personalizedlearningassistant.backend2.controller.profilecon;

import com.personalizedlearningassistant.backend2.dto.profiledtos.Profiledto;
import com.personalizedlearningassistant.backend2.services.profile.ProfileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileServices profileService;

    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Profiledto profiledto) {
        profileService.createProfile(profiledto);
        return ResponseEntity.ok().body("Profile created successfully");
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
