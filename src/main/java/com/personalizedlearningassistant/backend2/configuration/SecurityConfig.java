package com.personalizedlearningassistant.backend2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════════════
 * SPRING SECURITY CONFIGURATION - JWT COOKIE-BASED AUTHENTICATION
 * ═══════════════════════════════════════════════════════════════════════════════════
 *
 * Security Flow:
 * 1. User POST /api/auth/login → AuthController
 * 2. AuthController generates JWT and sets as HttpOnly cookie
 * 3. Browser sends cookie on every request (automatic)
 * 4. JwtAuthenticationFilter extracts cookie, validates JWT, sets SecurityContext
 * 5. Endpoint handler can check SecurityContextHolder for authenticated user
 * 6. On logout: all secrets revoked → all existing tokens instantly invalid
 *
 * CSRF: Disabled (not needed for stateless JWT + SameSite cookies)
 * CORS: Enabled with credentials for frontend at localhost:3000, 5173, 4200
 * Session: STATELESS (no session ID cookies, only JWT in access token cookie)
 * ═══════════════════════════════════════════════════════════════════════════════════
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * DAO Authentication Provider with BCrypt password encoding
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Authentication Manager bean - required for login endpoint
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * JWT Authentication Filter - instantiated as @Bean (NOT @Component on filter class)
     * This ensures it's only added to the Spring Security filter chain, not the servlet chain
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtility, customUserDetailsService);
    }

    /**
     * CORS Configuration
     * Allows cookies to be sent cross-origin (required for JWT in cookies)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from these origins
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",    // React dev server
                "http://localhost:5173",    // Vite dev server
                "http://localhost:4200"     // Angular dev server
        ));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Expose certain headers to frontend
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // CRITICAL FOR COOKIES: Must allow credentials
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Main Security Filter Chain
     * Order of matchers matters: most specific first, then .anyRequest() last
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ─────────────────────────────────────────────────────────────────────
                // CORS - Enable with the configuration above
                // ─────────────────────────────────────────────────────────────────────
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ─────────────────────────────────────────────────────────────────────
                // CSRF - Disabled for stateless JWT
                // ─────────────────────────────────────────────────────────────────────
                .csrf(csrf -> csrf.disable())

                // ─────────────────────────────────────────────────────────────────────
                // SESSION MANAGEMENT - Stateless (no session ID cookies)
                // ─────────────────────────────────────────────────────────────────────
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ─────────────────────────────────────────────────────────────────────
                // AUTHENTICATION PROVIDER
                // ─────────────────────────────────────────────────────────────────────
                .authenticationProvider(authenticationProvider())

                // ─────────────────────────────────────────────────────────────────────
                // AUTHORIZATION RULES
                // ─────────────────────────────────────────────────────────────────────
                .authorizeHttpRequests(authz -> authz
                        // OPTIONS preflight requests (CORS) - always allow
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public authentication endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/validate").permitAll()

                        // Health checks
                        .requestMatchers("/actuator/health").permitAll()

                        // Legacy endpoints (public)
                        .requestMatchers(HttpMethod.GET, "/home").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/create-profile").permitAll()

                        // Protected endpoints - require authentication
                        .requestMatchers("/api/profile/**").authenticated()
                        .requestMatchers("/api/learning-path/**").authenticated()
                        .requestMatchers("/api/diagnostic/**").authenticated()
                        .requestMatchers("/api/questions/**").authenticated()
                        .requestMatchers("/api/evaluate/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // ─────────────────────────────────────────────────────────────────────
                // JWT FILTER - Add BEFORE UsernamePasswordAuthenticationFilter
                // This ensures our JWT validation runs before form login processing
                // ─────────────────────────────────────────────────────────────────────
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

