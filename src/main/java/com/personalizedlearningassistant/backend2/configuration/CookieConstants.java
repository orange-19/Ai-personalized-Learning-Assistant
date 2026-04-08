package com.personalizedlearningassistant.backend2.configuration;

/**
 * Cookie constants shared between AuthController and JwtAuthFilter
 * Ensures consistent cookie names across the application
 */
public class CookieConstants {
    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    public static final int ACCESS_TOKEN_MAX_AGE = 900; // 15 minutes
    public static final int REFRESH_TOKEN_MAX_AGE = 604800; // 7 days
}

