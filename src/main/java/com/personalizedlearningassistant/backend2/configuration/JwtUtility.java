package com.personalizedlearningassistant.backend2.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtility {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtility.class);

    @Autowired
    private JwtSessionManager jwtSessionManager;

    @Value("${jwt.secret:}")
    private String configuredSecret;

    @Value("${jwt.use-dynamic-secret:true}")
    private boolean useDynamicSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    private String secretKey;

    /**
     * Initialize the secret key on bean creation.
     * If useDynamicSecret is true, generates a new SHA256-based key each session.
     * Otherwise, uses the configured secret from application.properties.
     */
    @PostConstruct
    public void initializeSecretKey() {
        if (useDynamicSecret) {
            // Initialize session manager with new session
            jwtSessionManager.initializeNewSession();
            secretKey = jwtSessionManager.getCurrentSecretKey();
            logger.info("Dynamic secret key generated for session: {}", jwtSessionManager.getCurrentSessionId());
            logger.info("Session created at: {}", jwtSessionManager.getSessionStartTime());
        } else if (configuredSecret != null && !configuredSecret.isEmpty()) {
            // Use configured secret from application.properties
            secretKey = configuredSecret;
            logger.info("Using configured secret key from application.properties");
        } else {
            // Fallback: generate a new key if neither is provided
            secretKey = SecretKeyGenerator.generateSecretKey();
            logger.warn("No configured secret provided, generating random secret key");
        }
    }

    /**
     * Generate JWT token from username
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
     * Create token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Validate JWT token without username
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}

