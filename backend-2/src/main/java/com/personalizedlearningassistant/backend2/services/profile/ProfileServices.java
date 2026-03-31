package com.personalizedlearningassistant.backend2.services.profile;

import com.personalizedlearningassistant.backend2.dto.profiledtos.Profiledto;
import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileServices {
    @Autowired
    private ProfileRepository profileRepository;

    public void createProfile(Profiledto profile){
        String username = profile.getUsername();
        String name = profile.getName();
        String rollno = profile.getRollno();
        String email = profile.getEmail();
        String password = profile.getPassword();
        String avatarUrl = profile.getAvatarUrl();
        UserProfile userProfile = new UserProfile(username, name, rollno, email, password, avatarUrl);
        profileRepository.save(userProfile);
    }

    public Profiledto getProfile(String username) {
        UserProfile userProfile = profileRepository.findByUsername(username);
        if (userProfile == null) {
            return null; // caller (controller) will handle 404
        }
        return new Profiledto(userProfile.getUsername(), userProfile.getName(), userProfile.getRollno(), userProfile.getEmail(), userProfile.getPassword(), userProfile.getAvatarUrl());
    }

    @Transactional
    public void updateProfile(String username,Profiledto profiledto){
        // Fetch the existing entity and update its fields so JPA performs an update (preserving id)
        UserProfile existing = profileRepository.findByUsername(username);

        if (existing == null) {
            throw new IllegalArgumentException("Profile not found for username: " + username);
        }

        Optional.ofNullable(profiledto.getName()).ifPresent(existing::setName);
        Optional.ofNullable(profiledto.getRollno()).ifPresent(existing::setRollno);
        Optional.ofNullable(profiledto.getEmail()).ifPresent(existing::setEmail);
        Optional.ofNullable(profiledto.getPassword()).ifPresent(existing::setPassword);
        Optional.ofNullable(profiledto.getAvatarUrl()).ifPresent(existing::setAvatarUrl);

        // no need to explicitly save inside transaction, but call save to be explicit
        profileRepository.save(existing);
    }
}
