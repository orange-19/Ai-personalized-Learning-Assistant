package com.personalizedlearningassistant.backend2.services;

import com.personalizedlearningassistant.backend2.dto.LoginRequest;
import com.personalizedlearningassistant.backend2.dto.LoginResponse;
import com.personalizedlearningassistant.backend2.dto.RegisterRequest;
import com.personalizedlearningassistant.backend2.dto.RegisterResponse;
import com.personalizedlearningassistant.backend2.model.RefreshToken;
import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.model.UserSessionSecret;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import com.personalizedlearningassistant.backend2.repository.RefreshTokenRepository;
import com.personalizedlearningassistant.backend2.configuration.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Authentication Service with per-user session secret support.
 * Each login creates a new session secret for the user.
 * On logout, all session secrets are revoked, instantly invalidating all tokens.
 */
@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserSessionSecretService userSessionSecretService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private long refreshTokenExpirationInMs;

    /**
     * Register a new user
     * 1. Check for duplicate username
     * 2. Encode password with BCrypt
     * 3. Save user to DB
     * 4. Generate session secret and tokens
     */
    public RegisterResponse register(RegisterRequest registerRequest) throws Exception {
        // Check for duplicate username
        UserProfile existingUser = profileRepository.findByUsername(registerRequest.getUsername());
        if (existingUser != null) {
            throw new Exception("Username already exists");
        }

        // Create new user profile
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(registerRequest.getUsername());
        userProfile.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userProfile.setEmail(registerRequest.getEmail());
        userProfile.setName(registerRequest.getFullName());

        // Save user to DB
        UserProfile savedUser = profileRepository.save(userProfile);

        // Generate new session secret for this user
        UserSessionSecret sessionSecret = userSessionSecretService.generateNewSecret(savedUser);
        logger.info("Generated session secret for new user: {}", savedUser.getUsername());

        // Generate access token (uses the new session secret internally)
        String accessToken = jwtUtility.generateToken(savedUser.getUsername());

        // Generate refresh token
        String refreshToken = jwtUtility.generateToken(savedUser.getUsername());
        LocalDateTime refreshTokenExpiry = LocalDateTime.now()
                .plusSeconds(refreshTokenExpirationInMs / 1000);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, savedUser, refreshTokenExpiry);
        refreshTokenRepository.save(refreshTokenEntity);

        return new RegisterResponse(
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getName(),
                "User registered successfully"
        );
    }

    /**
     * Login user
     * 1. Authenticate via AuthenticationManager
     * 2. Load UserDetails
     * 3. Generate new session secret (revoke old ones)
     * 4. Generate access token + refresh token
     */
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Load user profile
            UserProfile userProfile = profileRepository.findByUsername(loginRequest.getUsername());
            if (userProfile == null) {
                throw new Exception("User not found");
            }

            // Revoke all old session secrets and generate a new one
            userSessionSecretService.revokeAllSecrets(userProfile);
            UserSessionSecret newSessionSecret = userSessionSecretService.generateNewSecret(userProfile);
            logger.info("Generated new session secret for login: {}", userProfile.getUsername());

            // Generate access token (uses the new session secret)
            String accessToken = jwtUtility.generateToken(userProfile.getUsername());

            // Generate refresh token
            String refreshToken = jwtUtility.generateToken(userProfile.getUsername());
            LocalDateTime refreshTokenExpiry = LocalDateTime.now()
                    .plusSeconds(refreshTokenExpirationInMs / 1000);
            RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, userProfile, refreshTokenExpiry);
            refreshTokenRepository.save(refreshTokenEntity);

            return new LoginResponse(
                    userProfile.getUsername(),
                    "USER", // Default role
                    "Login successful"
            );
        } catch (Exception e) {
            throw new Exception("Invalid username or password");
        }
    }

    /**
     * Refresh token
     * 1. Read refresh_token from request
     * 2. Look up token in DB
     * 3. Check not expired and not revoked
     * 4. Generate new access token
     */
    public String refreshAccessToken(String refreshToken) throws Exception {
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (!tokenEntity.isPresent()) {
            throw new Exception("Refresh token not found");
        }

        RefreshToken token = tokenEntity.get();

        // Check if expired or revoked
        if (token.isExpired() || token.isRevoked()) {
            throw new Exception("Refresh token is expired or revoked");
        }

        // Generate new access token using user's current session secret
        String newAccessToken = jwtUtility.generateToken(token.getUserProfile().getUsername());

        return newAccessToken;
    }

    /**
     * Logout user
     * 1. Revoke all session secrets (instantly invalidates all tokens)
     * 2. Delete refresh token from DB
     */
    public void logout(String refreshToken) throws Exception {
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);

        if (tokenEntity.isPresent()) {
            UserProfile userProfile = tokenEntity.get().getUserProfile();

            // Revoke all session secrets for this user (instant token invalidation)
            userSessionSecretService.revokeAllSecrets(userProfile);
            logger.info("Revoked all session secrets for logout: {}", userProfile.getUsername());

            // Delete refresh token from DB
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }
}

