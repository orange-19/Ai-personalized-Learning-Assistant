package com.personalizedlearningassistant.backend2.repository;

import com.personalizedlearningassistant.backend2.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUsername(String username);
}
