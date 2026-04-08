package com.personalizedlearningassistant.backend2.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════════════════════
 * JWT AUTHENTICATION FILTER - PER-USER SESSION SECRET VERSION
 * ═══════════════════════════════════════════════════════════════════════════════════
 *
 * DEBUG CHECKLIST - Read logs for each step:
 * ✓ Step 1: [JWT_FILTER_DEBUG] Request URI: /api/profile/me
 * ✓ Step 2: [JWT_FILTER_DEBUG] Cookies present: YES (length=2)
 * ✓ Step 3: [JWT_FILTER_DEBUG] Token found: YES (length=350)
 * ✓ Step 4: [JWT_FILTER_DEBUG] Public endpoint: NO
 * ✓ Step 5: [JWT_FILTER_DEBUG] Username extracted: john_doe
 * ✓ Step 6: [JWT_FILTER_DEBUG] User profile found: YES
 * ✓ Step 7: [JWT_FILTER_DEBUG] Active secret found: YES
 * ✓ Step 8: [JWT_FILTER_DEBUG] Token validation: PASSED
 * ✓ Step 9: [JWT_FILTER_DEBUG] UserDetails loaded: YES (authorities=[ROLE_USER])
 * ✓ Step 10: [JWT_FILTER_DEBUG] SecurityContext.setAuthentication(): DONE
 * ✓ Step 11: [JWT_FILTER_DEBUG] chain.doFilter() called: proceeding to controller
 *
 * If any step shows FAILED:
 * - Step 3 FAILED → Cookie not sent by browser (check CORS allowCredentials, SameSite)
 * - Step 4 FAILED → Secret not in DB (check if login actually saved secret)
 * - Step 5 FAILED → Token malformed (check if token was generated correctly)
 * - Step 8 FAILED → See JwtUtility debug logs for signature/expiration details
 * - Step 10 FAILED → UserDetailsService not returning proper authorities
 * ═══════════════════════════════════════════════════════════════════════════════════
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtility jwtUtility;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Constructor injection - Spring will instantiate via SecurityConfig
     */
    public JwtAuthenticationFilter(JwtUtility jwtUtility,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtUtility = jwtUtility;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getServletPath();

        logger.debug("[JWT_FILTER_DEBUG] ════════════════════════════════════════");
        logger.debug("[JWT_FILTER_DEBUG] Step 1: Request URI: {}", requestPath);

        // ─────────────────────────────────────────────────────────────────────
        // SKIP JWT FILTER FOR PUBLIC ENDPOINTS
        // ─────────────────────────────────────────────────────────────────────
        if (isPublicEndpoint(requestPath)) {
            logger.debug("[JWT_FILTER_DEBUG] Step 4: Public endpoint: YES → skipping JWT filter");
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("[JWT_FILTER_DEBUG] Step 4: Public endpoint: NO → processing JWT");

        try {
            // ─────────────────────────────────────────────────────────────────────
            // STEP 2: EXTRACT TOKEN FROM COOKIE
            // ─────────────────────────────────────────────────────────────────────
            String jwt = extractTokenFromCookie(request);

            if (request.getCookies() != null) {
                logger.debug("[JWT_FILTER_DEBUG] Step 2: Cookies present: YES (count={})", request.getCookies().length);
            } else {
                logger.debug("[JWT_FILTER_DEBUG] Step 2: Cookies present: NO");
            }

            if (StringUtils.hasText(jwt)) {
                logger.debug("[JWT_FILTER_DEBUG] Step 3: Token found: YES (length={})", jwt.length());
            } else {
                logger.debug("[JWT_FILTER_DEBUG] Step 3: Token found: NO → returning 403");
                filterChain.doFilter(request, response);
                return;
            }

            // ─────────────────────────────────────────────────────────────────────
            // STEP 3: EXTRACT USERNAME (UNSAFE - NO VERIFICATION YET)
            // ─────────────────────────────────────────────────────────────────────
            String username = jwtUtility.extractUsername(jwt);

            if (StringUtils.hasText(username)) {
                logger.debug("[JWT_FILTER_DEBUG] Step 5: Username extracted: {}", username);
            } else {
                logger.debug("[JWT_FILTER_DEBUG] Step 5: Username extraction FAILED");
                filterChain.doFilter(request, response);
                return;
            }

            // ─────────────────────────────────────────────────────────────────────
            // STEP 4: ONLY PROCEED IF NO AUTHENTICATION ALREADY SET
            // ─────────────────────────────────────────────────────────────────────
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.debug("[JWT_FILTER_DEBUG] Step 6: SecurityContext empty → proceeding with auth");

                // ─────────────────────────────────────────────────────────────────────
                // STEP 5: VALIDATE TOKEN (full signature verification)
                // ─────────────────────────────────────────────────────────────────────
                if (jwtUtility.validateToken(jwt)) {
                    logger.debug("[JWT_FILTER_DEBUG] Step 8: Token validation: PASSED");

                    // ─────────────────────────────────────────────────────────────────────
                    // STEP 6: LOAD USER DETAILS FROM DATABASE
                    // ─────────────────────────────────────────────────────────────────────
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    if (userDetails != null) {
                        logger.debug("[JWT_FILTER_DEBUG] Step 9: UserDetails loaded: YES (authorities={})",
                                userDetails.getAuthorities());
                    } else {
                        logger.debug("[JWT_FILTER_DEBUG] Step 9: UserDetails load FAILED");
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // ─────────────────────────────────────────────────────────────────────
                    // STEP 7: CREATE AND SET AUTHENTICATION TOKEN
                    // ─────────────────────────────────────────────────────────────────────
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()  // ← MUST NOT BE NULL/EMPTY
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("[JWT_FILTER_DEBUG] Step 10: SecurityContext.setAuthentication(): DONE");

                } else {
                    logger.debug("[JWT_FILTER_DEBUG] Step 8: Token validation: FAILED → see JwtUtility logs");
                }
            } else {
                logger.debug("[JWT_FILTER_DEBUG] Step 6: SecurityContext already has auth → skipping");
            }

        } catch (Exception ex) {
            logger.error("[JWT_FILTER_ERROR] Unexpected error in JWT filter: {}", ex.getMessage(), ex);
        }

        logger.debug("[JWT_FILTER_DEBUG] Step 11: chain.doFilter() called: proceeding to controller");
        logger.debug("[JWT_FILTER_DEBUG] ════════════════════════════════════════");

        // ─────────────────────────────────────────────────────────────────────
        // CRITICAL: ALWAYS call chain.doFilter, even if authentication failed
        // ─────────────────────────────────────────────────────────────────────
        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request path is for a public endpoint that doesn't require JWT
     */
    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api/auth/") ||
                requestPath.startsWith("/api/users/") ||
                requestPath.startsWith("/api/public/") ||
                requestPath.startsWith("/api/jwt-info/") ||
                requestPath.equals("/login") ||
                requestPath.equals("/register") ||
                requestPath.equals("/api/login") ||
                requestPath.equals("/api/register") ||
                requestPath.equals("/create-profile") ||
                requestPath.equals("/home") ||
                requestPath.equals("/actuator/health") ||
                requestPath.equals("/");
    }

    /**
     * Extract JWT token from the accessToken cookie
     * Safe cookie extraction with null checks
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            logger.debug("[JWT_FILTER_DEBUG] No cookies in request");
            return null;
        }

        Optional<String> token = Arrays.stream(request.getCookies())
                .filter(cookie -> CookieConstants.ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();

        if (token.isPresent()) {
            logger.debug("[JWT_FILTER_DEBUG] Found {} cookie", CookieConstants.ACCESS_TOKEN_COOKIE);
            return token.get();
        } else {
            logger.debug("[JWT_FILTER_DEBUG] {} cookie not found in request", CookieConstants.ACCESS_TOKEN_COOKIE);
            return null;
        }
    }
}

