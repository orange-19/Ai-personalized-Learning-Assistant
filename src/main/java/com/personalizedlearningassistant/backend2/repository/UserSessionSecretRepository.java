package com.personalizedlearningassistant.backend2.repository;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.model.UserSessionSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionSecretRepository extends JpaRepository<UserSessionSecret, Long> {

    /**
     * Find the active (valid, non-revoked) secret for a user
     */
    @Query("SELECT s FROM user_session_secrets s WHERE s.userProfile = :userProfile " +
           "AND s.revoked = false AND s.expiresAt > CURRENT_TIMESTAMP " +
           "ORDER BY s.createdAt DESC LIMIT 1")
    Optional<UserSessionSecret> findActiveSecretByUser(@Param("userProfile") UserProfile userProfile);

    /**
     * Find all secrets for a user (including expired/revoked)
     */
    List<UserSessionSecret> findByUserProfile(UserProfile userProfile);

    /**
     * Find a specific secret by key
     */
    Optional<UserSessionSecret> findBySecretKey(String secretKey);

    /**
     * Find active secrets for a user (used for logout to revoke all sessions)
     */
    @Query("SELECT s FROM user_session_secrets s WHERE s.userProfile = :userProfile " +
           "AND s.revoked = false")
    List<UserSessionSecret> findActiveSecretsByUser(@Param("userProfile") UserProfile userProfile);
}

