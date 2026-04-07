package com.personalizedlearningassistant.backend2.configuration;

import javax.crypto.KeyGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates cryptographically secure secret keys for JWT signing.
 * Uses SHA256 hashing and SecureRandom for high entropy.
 */
public class SecretKeyGenerator {

    /**
     * Generate a random secret key using SHA256
     * The key is base64 encoded for safe storage/transmission
     *
     * @return Base64 encoded secret key suitable for HS256 signing
     */
    public static String generateSecretKey() {
        try {
            // Generate 32 bytes of random data (256 bits for HS256)
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);

            // Hash the random bytes with SHA256 for additional security
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(randomBytes);

            // Base64 encode the hashed bytes
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Generate a secret key with timestamp for additional uniqueness
     * Format: Base64(SHA256(randomBytes + timestamp))
     *
     * @return Base64 encoded secret key with timestamp component
     */
    public static String generateSecretKeyWithTimestamp() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);

            // Combine random bytes with current timestamp
            long timestamp = System.currentTimeMillis();
            String combined = Base64.getEncoder().encodeToString(randomBytes) + timestamp;

            // Hash with SHA256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(combined.getBytes());

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Generate a secret key with custom seed
     * Useful for deterministic generation if needed
     *
     * @param seed Custom seed for key generation
     * @return Base64 encoded secret key
     */
    public static String generateSecretKey(String seed) {
        try {
            // Combine seed with random data
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);

            String combined = seed + Base64.getEncoder().encodeToString(randomBytes);

            // Hash with SHA256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(combined.getBytes());

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Main method to test key generation
     */
    public static void main(String[] args) {
        System.out.println("=== JWT Secret Key Generation ===\n");

        System.out.println("1. Simple Random Key:");
        System.out.println("   " + generateSecretKey());
        System.out.println();

        System.out.println("2. Key with Timestamp:");
        System.out.println("   " + generateSecretKeyWithTimestamp());
        System.out.println();

        System.out.println("3. Key with Custom Seed:");
        System.out.println("   " + generateSecretKey("my-app-salt"));
        System.out.println();

        System.out.println("Generated keys are Base64 encoded SHA256 hashes (44 characters)");
    }
}

