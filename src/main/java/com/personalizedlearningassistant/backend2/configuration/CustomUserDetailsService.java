package com.personalizedlearningassistant.backend2.configuration;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom UserDetailsService that loads user details from UserProfile
 * CRITICAL: Must return UserDetails with at least one authority (ROLE_USER)
 * Empty authorities list causes 403 Forbidden on protected endpoints
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("[USERDETAILS] Loading user details for username: {}", username);

        UserProfile userProfile = profileRepository.findByUsername(username);

        if (userProfile == null) {
            logger.error("[USERDETAILS] User not found: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        logger.debug("[USERDETAILS] User found: {} (ID: {})", username, userProfile.getId());

        // Get user authorities (roles/permissions)
        // CRITICAL: Must have at least one authority or Spring Security will deny access
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Add default ROLE_USER for all authenticated users
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        logger.debug("[USERDETAILS] User {} loaded with authorities: {}", username, authorities);

        return new User(
                userProfile.getUsername(),
                userProfile.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities  // ← MUST NOT BE EMPTY
        );
    }
}

