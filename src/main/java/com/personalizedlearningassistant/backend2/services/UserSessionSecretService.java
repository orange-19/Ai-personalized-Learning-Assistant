package com.personalizedlearningassistant.backend2.services;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.model.UserSessionSecret;
import com.personalizedlearningassistant.backend2.repository.UserSessionSecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing per-user session secrets.
 * Each user gets a unique cryptographically secure secret key for their session.
 * Secrets are stored server-side and tied to the user's record.
 */
@Service
public class UserSessionSecretService {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionSecretService.class);

    @Autowired
    private UserSessionSecretRepository userSessionSecretRepository;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    private static final int SECRET_KEY_LENGTH = 64; // 512 bits

    /**
     * Generate a new cryptographically secure random secret for a user
     */
    public UserSessionSecret generateNewSecret(UserProfile userProfile) {
        // Generate 512-bit random secret
        byte[] randomBytes = new byte[SECRET_KEY_LENGTH];
        new SecureRandom().nextBytes(randomBytes);
        String secretKey = Base64.getEncoder().encodeToString(randomBytes);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(jwtExpirationInMs / 1000);

        UserSessionSecret sessionSecret = new UserSessionSecret(userProfile, secretKey, now, expiresAt);
        UserSessionSecret saved = userSessionSecretRepository.save(sessionSecret);

        logger.info("Generated new session secret for user: {}", userProfile.getUsername());
        return saved;
    }

    /**
     * Get the active secret for a user
     * Returns the most recent non-revoked, non-expired secret
     */
    public Optional<UserSessionSecret> getActiveSecret(UserProfile userProfile) {
        return userSessionSecretRepository.findActiveSecretByUser(userProfile);
    }

    /**
     * Get or create a secret for a user
     * If an active secret exists, returns it. Otherwise, generates a new one.
     */
    public UserSessionSecret getOrCreateSecret(UserProfile userProfile) {
        Optional<UserSessionSecret> existing = getActiveSecret(userProfile);
        if (existing.isPresent()) {
            return existing.get();
        }
        return generateNewSecret(userProfile);
    }

    /**
     * Find a secret by its key value
     */
    public Optional<UserSessionSecret> findBySecretKey(String secretKey) {
        return userSessionSecretRepository.findBySecretKey(secretKey);
    }

    /**
     * Revoke (invalidate) all active secrets for a user
     * Called on logout to instantly invalidate all tokens
     */
    public void revokeAllSecrets(UserProfile userProfile) {
        List<UserSessionSecret> activeSecrets = userSessionSecretRepository.findActiveSecretsByUser(userProfile);
        for (UserSessionSecret secret : activeSecrets) {
            secret.setRevoked(true);
            userSessionSecretRepository.save(secret);
        }
        logger.info("Revoked all session secrets for user: {}", userProfile.getUsername());
    }

    /**
     * Revoke a specific secret
     */
    public void revokeSecret(UserSessionSecret secret) {
        secret.setRevoked(true);
        userSessionSecretRepository.save(secret);
        logger.info("Revoked session secret for user: {}", secret.getUserProfile().getUsername());
    }

    /**
     * Check if a secret is valid and not expired
     */
    public boolean isSecretValid(UserSessionSecret secret) {
        return secret != null && secret.isValid();
    }

    /**
     * Clean up expired secrets (optional maintenance task)
     */
    public void cleanupExpiredSecrets() {
        List<UserSessionSecret> allSecrets = userSessionSecretRepository.findAll();
        int revokedCount = 0;
        for (UserSessionSecret secret : allSecrets) {
            if (!secret.isValid() && !secret.isRevoked()) {
                secret.setRevoked(true);
                userSessionSecretRepository.save(secret);
                revokedCount++;
            }
        }
        logger.info("Cleanup complete: marked {} expired secrets as revoked", revokedCount);
    }
}

