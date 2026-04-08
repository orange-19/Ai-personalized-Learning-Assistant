package com.personalizedlearningassistant.backend2.controller;

import com.personalizedlearningassistant.backend2.configuration.CookieConstants;
import com.personalizedlearningassistant.backend2.configuration.JwtUtility;
import com.personalizedlearningassistant.backend2.dto.*;
import com.personalizedlearningassistant.backend2.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtility jwtUtility;

    /**
     * Registration endpoint - creates a new user account
     * POST /api/auth/register
     * 1. Accept RegisterRequest DTO with: username, password, email, fullName
     * 2. Check for duplicate username → return 409 if exists
     * 3. Encode password with BCrypt
     * 4. Save user to DB
     * 5. Generate access token + refresh token
     * 6. Set both as HttpOnly cookies
     * 7. Return 201 with user info in body
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest,
            HttpServletResponse response) {
        try {
            RegisterResponse registerResponse = authenticationService.register(registerRequest);

            // Set access token and refresh token as HttpOnly cookies
            setTokenCookies(response, registerRequest.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);

        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Username already exists", 409));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration failed: " + e.getMessage(), 500));
        }
    }

    /**
     * Login endpoint - authenticates user and returns JWT tokens
     * POST /api/auth/login
     * 1. Accept LoginRequest DTO with: username, password
     * 2. Authenticate via AuthenticationManager
     * 3. Load UserDetails
     * 4. Generate access token (15 min) + refresh token (7 days)
     * 5. Save refresh token to DB
     * 6. Set both tokens as HttpOnly cookies
     * 7. Return 200 with username and role
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authenticationService.login(loginRequest);

            // Set access token and refresh token as HttpOnly cookies
            setTokenCookies(response, loginRequest.getUsername());

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password", 401));
        }
    }

    /**
     * Refresh token endpoint
     * POST /api/auth/refresh
     * 1. Read refresh_token from cookie
     * 2. Look up token in DB
     * 3. Check not expired and not revoked
     * 4. Generate new access token
     * 5. Optionally rotate refresh token (delete old, create new)
     * 6. Set new tokens as cookies
     * 7. Return 200
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Refresh token not found", 401));
            }

            String newAccessToken = authenticationService.refreshAccessToken(refreshToken);

            // Set new tokens as HttpOnly cookies
            response.addHeader("Set-Cookie",
                    "access_token=" + newAccessToken + "; Path=/; HttpOnly; SameSite=Lax; Max-Age=900");
            response.addHeader("Set-Cookie",
                    "refresh_token=" + refreshToken + "; Path=/; HttpOnly; SameSite=Lax; Max-Age=604800");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Token refreshed successfully");
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token refresh failed: " + e.getMessage(), 401));
        }
    }

    /**
     * Logout endpoint
     * POST /api/auth/logout
     * 1. Read refresh_token from cookie
     * 2. Delete from DB
     * 3. Clear both cookies (set maxAge=0)
     * 4. Return 200
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        try {
            if (refreshToken != null && !refreshToken.isEmpty()) {
                authenticationService.logout(refreshToken);
            }

            // Clear both cookies
            response.addHeader("Set-Cookie", "access_token=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0");
            response.addHeader("Set-Cookie", "refresh_token=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Logout successful");
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Logout failed: " + e.getMessage(), 500));
        }
    }

    /**
     * Validate token endpoint
     * GET /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("No token provided", 401));
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtility.validateToken(token)) {
                String username = jwtUtility.extractUsername(token);
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("valid", true);
                responseBody.put("username", username);
                responseBody.put("message", "Token is valid");
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Token is invalid or expired", 401));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token validation failed: " + e.getMessage(), 401));
        }
    }

    /**
     * Helper method to set both access and refresh tokens as HttpOnly cookies
     * Uses CookieConstants for consistent naming across filters
     */
    private void setTokenCookies(HttpServletResponse response, String username) {
        String accessToken = jwtUtility.generateToken(username);
        String refreshToken = jwtUtility.generateToken(username);

        // Set access token cookie (15 minutes = 900 seconds)
        // HttpOnly: prevents JavaScript access
        // Secure: false for localhost dev, true for HTTPS production
        // SameSite=Lax: prevents CSRF while allowing normal navigation
        response.addHeader("Set-Cookie",
                CookieConstants.ACCESS_TOKEN_COOKIE + "=" + accessToken +
                "; Path=/; HttpOnly; SameSite=Lax; Max-Age=" + CookieConstants.ACCESS_TOKEN_MAX_AGE);

        // Set refresh token cookie (7 days = 604800 seconds)
        response.addHeader("Set-Cookie",
                CookieConstants.REFRESH_TOKEN_COOKIE + "=" + refreshToken +
                "; Path=/; HttpOnly; SameSite=Lax; Max-Age=" + CookieConstants.REFRESH_TOKEN_MAX_AGE);
    }

    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private String message;
        private int status;

        public ErrorResponse(String message, int status) {
            this.message = message;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}

