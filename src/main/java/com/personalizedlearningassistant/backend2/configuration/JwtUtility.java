package com.personalizedlearningassistant.backend2.configuration;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.model.UserSessionSecret;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import com.personalizedlearningassistant.backend2.services.UserSessionSecretService;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JWT Utility with per-user session secret support (JJWT 0.12.3)
 *
 * Security Architecture:
 * - Each user gets a UNIQUE secret key on login, stored server-side
 * - Tokens signed with that user's secret
 * - On logout, secret revoked → all user's tokens instantly invalid
 * - No global secret → compromise of one user ≠ all users compromised
 */
@Component
public class JwtUtility {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtility.class);

    @Autowired
    private UserSessionSecretService userSessionSecretService;

    @Autowired
    private ProfileRepository profileRepository;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    /**
     * Generate JWT token for a user using their session-specific secret
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(String username, Map<String, Object> claims) {
        claims.put("username", username);
        return createToken(claims, username);
    }

    /**
     * Create token with claims using user's session secret
     */
    private String createToken(Map<String, Object> claims, String username) {
        logger.debug("[JWT_GENERATE] Creating token for user: {}", username);

        // Find the user
        UserProfile user = profileRepository.findByUsername(username);
        if (user == null) {
            logger.error("[JWT_GENERATE] User not found: {}", username);
            throw new IllegalArgumentException("User not found: " + username);
        }

        // Get or create the user's session secret
        UserSessionSecret sessionSecret = userSessionSecretService.getOrCreateSecret(user);
        logger.debug("[JWT_GENERATE] Using session secret ID: {}", sessionSecret.getId());

        SecretKey key = Keys.hmacShaKeyFor(sessionSecret.getSecretKey().getBytes());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        logger.debug("[JWT_GENERATE] Token created successfully (length: {})", token.length());
        return token;
    }

    /**
     * Extract username from JWT token WITHOUT verification
     * Safe to call with untrusted tokens (we verify later)
     */
    public String extractUsername(String token) {
        try {
            // Split JWT into parts: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                logger.debug("[JWT_EXTRACT] Invalid JWT structure (parts: {})", parts.length);
                return null;
            }

            // Decode the payload (second part) from Base64URL
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            logger.debug("[JWT_EXTRACT] Payload decoded");

            // Extract "sub" (subject/username) from JSON
            if (payload.contains("\"sub\":\"")) {
                int start = payload.indexOf("\"sub\":\"") + 7;
                int end = payload.indexOf("\"", start);
                String username = payload.substring(start, end);
                logger.debug("[JWT_EXTRACT] Username extracted: {}", username);
                return username;
            }

            logger.debug("[JWT_EXTRACT] No 'sub' field in payload");
            return null;

        } catch (Exception e) {
            logger.error("[JWT_EXTRACT] Failed to extract username: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract expiration from JWT token WITHOUT verification
     */
    public Date extractExpiration(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            // Extract "exp" (expiration) from JSON
            if (payload.contains("\"exp\":")) {
                int start = payload.indexOf("\"exp\":") + 6;
                int end = payload.indexOf(",", start);
                if (end == -1) {
                    end = payload.indexOf("}", start);
                }
                String expStr = payload.substring(start, end).trim();
                long expTime = Long.parseLong(expStr) * 1000; // Convert to milliseconds
                return new Date(expTime);
            }

            return null;

        } catch (Exception e) {
            logger.error("[JWT_EXTRACT] Failed to extract expiration: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration == null) {
                logger.debug("[JWT_VALIDATE] Could not extract expiration");
                return true;
            }

            boolean expired = expiration.before(new Date());
            if (expired) {
                logger.debug("[JWT_VALIDATE] Token EXPIRED at: {}", expiration);
            } else {
                logger.debug("[JWT_VALIDATE] Token expires at: {}", expiration);
            }
            return expired;

        } catch (Exception e) {
            logger.error("[JWT_VALIDATE] Error checking expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════════════════
     * VALIDATE TOKEN - FULL PROCESS WITH DEBUGGING
     * ═══════════════════════════════════════════════════════════════════════════════════
     *
     * This is the critical validation function called by JwtAuthFilter.
     * Debug output tells you exactly where validation fails.
     */
    public Boolean validateToken(String token) {
        logger.debug("[JWT_VALIDATE] ════════════════════════════════════════");
        logger.debug("[JWT_VALIDATE] Starting token validation");

        try {
            // Step 1: Extract username from payload (unsigned)
            logger.debug("[JWT_VALIDATE] Step 1: Extracting username from payload...");
            String username = extractUsername(token);

            if (username == null) {
                logger.warn("[JWT_VALIDATE] ✗ Step 1 FAILED: Could not extract username from token");
                return false;
            }
            logger.debug("[JWT_VALIDATE] ✓ Step 1 PASSED: Username = {}", username);

            // Step 2: Find user in database
            logger.debug("[JWT_VALIDATE] Step 2: Looking up user in database...");
            UserProfile user = profileRepository.findByUsername(username);

            if (user == null) {
                logger.warn("[JWT_VALIDATE] ✗ Step 2 FAILED: User not found in DB: {}", username);
                return false;
            }
            logger.debug("[JWT_VALIDATE] ✓ Step 2 PASSED: User found (ID: {})", user.getId());

            // Step 3: Get user's active session secret
            logger.debug("[JWT_VALIDATE] Step 3: Fetching active session secret...");
            Optional<UserSessionSecret> sessionSecret = userSessionSecretService.getActiveSecret(user);

            if (!sessionSecret.isPresent()) {
                logger.warn("[JWT_VALIDATE] ✗ Step 3 FAILED: No active session secret for user: {}", username);
                logger.warn("[JWT_VALIDATE]   Hint: User may not have logged in recently, or session expired");
                return false;
            }
            logger.debug("[JWT_VALIDATE] ✓ Step 3 PASSED: Session secret found (ID: {})", sessionSecret.get().getId());

            UserSessionSecret secret = sessionSecret.get();

            // Step 4: Check if session secret is valid
            logger.debug("[JWT_VALIDATE] Step 4: Checking session secret validity...");
            if (!secret.isValid()) {
                logger.warn("[JWT_VALIDATE] ✗ Step 4 FAILED: Session secret is revoked or expired");
                logger.warn("[JWT_VALIDATE]   Revoked: {}, Expires: {}", secret.isRevoked(), secret.getExpiresAt());
                return false;
            }
            logger.debug("[JWT_VALIDATE] ✓ Step 4 PASSED: Session secret is valid");

            // Step 5: Verify JWT signature with user's secret
            logger.debug("[JWT_VALIDATE] Step 5: Verifying JWT signature with user's secret...");
            try {
                SecretKey key = Keys.hmacShaKeyFor(secret.getSecretKey().getBytes());
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token);

                logger.debug("[JWT_VALIDATE] ✓ Step 5 PASSED: JWT signature verified");

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("[JWT_VALIDATE] ✗ Step 5 FAILED: JWT EXPIRED - {}", e.getMessage());
                return false;
            } catch (io.jsonwebtoken.SignatureException e) {
                logger.warn("[JWT_VALIDATE] ✗ Step 5 FAILED: SIGNATURE INVALID - {}", e.getMessage());
                logger.warn("[JWT_VALIDATE]   Hint: Token was signed with different secret (old session?)");
                return false;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.warn("[JWT_VALIDATE] ✗ Step 5 FAILED: JWT MALFORMED - {}", e.getMessage());
                return false;
            } catch (Exception e) {
                logger.warn("[JWT_VALIDATE] ✗ Step 5 FAILED: Signature verification error - {}", e.getClass().getSimpleName());
                logger.warn("[JWT_VALIDATE]   Message: {}", e.getMessage());
                return false;
            }

            // Step 6: Check expiration
            logger.debug("[JWT_VALIDATE] Step 6: Checking token expiration...");
            if (isTokenExpired(token)) {
                logger.warn("[JWT_VALIDATE] ✗ Step 6 FAILED: Token is expired");
                return false;
            }
            logger.debug("[JWT_VALIDATE] ✓ Step 6 PASSED: Token is not expired");

            logger.debug("[JWT_VALIDATE] ✓✓✓ ALL STEPS PASSED - TOKEN IS VALID ✓✓✓");
            logger.debug("[JWT_VALIDATE] ════════════════════════════════════════");
            return true;

        } catch (Exception e) {
            logger.error("[JWT_VALIDATE] ✗ UNEXPECTED ERROR: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            logger.error("[JWT_VALIDATE] ════════════════════════════════════════");
            return false;
        }
    }

    /**
     * Validate JWT token with username (secondary check)
     */
    public Boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            if (extractedUsername == null || !extractedUsername.equals(username)) {
                logger.warn("[JWT_VALIDATE] Username mismatch: expected={}, got={}", username, extractedUsername);
                return false;
            }
            return validateToken(token);
        } catch (Exception e) {
            logger.error("[JWT_VALIDATE] Error validating token with username: {}", e.getMessage());
            return false;
        }
    }
}

