package com.personalizedlearningassistant.backend2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Per-user session secret storage.
 * Each user gets a unique secret key for their session.
 * When the user logs out or a new session starts, the old secret is deleted.
 * This achieves stateless JWT with stateful secret management.
 */
@Entity(name = "user_session_secrets")
public class UserSessionSecret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false, length = 1024)
    private String secretKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    public UserSessionSecret() {}

    public UserSessionSecret(UserProfile userProfile, String secretKey, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.userProfile = userProfile;
        this.secretKey = secretKey;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    /**
     * Check if the secret is still valid (not expired and not revoked)
     */
    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }
}

