package com.personalizedlearningassistant.backend2.configuration;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfile userProfile = profileRepository.findByUsername(username);

        if (userProfile == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Get user authorities (roles/permissions)
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // Add roles as needed (e.g., ROLE_USER, ROLE_ADMIN)
        // authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(
                userProfile.getUsername(),
                userProfile.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}

