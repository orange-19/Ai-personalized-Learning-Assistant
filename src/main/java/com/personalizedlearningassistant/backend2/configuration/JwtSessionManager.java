package com.personalizedlearningassistant.backend2.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages session-specific secret keys and metadata.
 * Useful for tracking when keys were generated and their validity.
 */
@Component
public class JwtSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(JwtSessionManager.class);

    private final Map<String, SessionKeyData> sessionKeyMap = new HashMap<>();
    private String currentSessionId;
    private String currentSecretKey;
    private Instant sessionStartTime;

    /**
     * Initialize a new session with a generated secret key
     */
    public synchronized void initializeNewSession() {
        currentSessionId = generateSessionId();
        currentSecretKey = SecretKeyGenerator.generateSecretKeyWithTimestamp();
        sessionStartTime = Instant.now();

        // Store session metadata
        SessionKeyData keyData = new SessionKeyData(
                currentSessionId,
                currentSecretKey,
                sessionStartTime
        );
        sessionKeyMap.put(currentSessionId, keyData);

        logger.info("New session created - SessionID: {}, Time: {}", currentSessionId, sessionStartTime);
    }

    /**
     * Get the current session ID
     */
    public String getCurrentSessionId() {
        return currentSessionId;
    }

    /**
     * Get the current session's secret key
     */
    public String getCurrentSecretKey() {
        return currentSecretKey;
    }

    /**
     * Get the current session start time
     */
    public Instant getSessionStartTime() {
        return sessionStartTime;
    }

    /**
     * Get session data by session ID
     */
    public SessionKeyData getSessionData(String sessionId) {
        return sessionKeyMap.get(sessionId);
    }

    /**
     * Check if a session exists
     */
    public boolean sessionExists(String sessionId) {
        return sessionKeyMap.containsKey(sessionId);
    }

    /**
     * Get the number of active sessions
     */
    public int getActiveSessions() {
        return sessionKeyMap.size();
    }

    /**
     * Clear all sessions (useful for application restart)
     */
    public synchronized void clearAllSessions() {
        sessionKeyMap.clear();
        logger.info("All sessions cleared");
    }

    /**
     * Generate a unique session ID based on timestamp and random UUID
     */
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" +
               java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Inner class to hold session key metadata
     */
    public static class SessionKeyData {
        private final String sessionId;
        private final String secretKey;
        private final Instant createdAt;

        public SessionKeyData(String sessionId, String secretKey, Instant createdAt) {
            this.sessionId = sessionId;
            this.secretKey = secretKey;
            this.createdAt = createdAt;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }
    }
}

