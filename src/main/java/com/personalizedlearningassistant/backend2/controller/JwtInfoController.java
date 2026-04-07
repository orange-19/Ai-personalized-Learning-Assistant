package com.personalizedlearningassistant.backend2.controller;

import com.personalizedlearningassistant.backend2.configuration.JwtSessionManager;
import com.personalizedlearningassistant.backend2.configuration.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jwt-info")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class JwtInfoController {

    @Autowired
    private JwtSessionManager jwtSessionManager;

    @Autowired
    private JwtUtility jwtUtility;

    /**
     * Get current session and secret key information
     * Useful for debugging and monitoring
     */
    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionId", jwtSessionManager.getCurrentSessionId());
        info.put("sessionStartTime", jwtSessionManager.getSessionStartTime());
        info.put("activeSessions", jwtSessionManager.getActiveSessions());
        info.put("secretKeyGenerated", jwtSessionManager.getCurrentSecretKey() != null);
        info.put("keyLength", jwtSessionManager.getCurrentSecretKey() != null ?
                jwtSessionManager.getCurrentSecretKey().length() : 0);

        return ResponseEntity.ok(info);
    }

    /**
     * Display masked secret key (only last 8 characters visible for security)
     */
    @GetMapping("/secret-key-masked")
    public ResponseEntity<?> getMaskedSecretKey() {
        String fullKey = jwtSessionManager.getCurrentSecretKey();
        String maskedKey = "";

        if (fullKey != null && fullKey.length() > 8) {
            maskedKey = "*".repeat(fullKey.length() - 8) + fullKey.substring(fullKey.length() - 8);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("maskedSecretKey", maskedKey);
        response.put("sessionId", jwtSessionManager.getCurrentSessionId());
        response.put("message", "Secret key is masked for security. Only last 8 characters are visible.");

        return ResponseEntity.ok(response);
    }

    /**
     * Health check for JWT configuration
     */
    @GetMapping("/health")
    public ResponseEntity<?> jwtHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("sessionId", jwtSessionManager.getCurrentSessionId());
        health.put("jwtConfigured", true);
        health.put("dynamicKeyEnabled", true);
        health.put("activeSessions", jwtSessionManager.getActiveSessions());

        return ResponseEntity.ok(health);
    }
}

