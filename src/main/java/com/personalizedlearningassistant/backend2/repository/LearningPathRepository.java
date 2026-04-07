package com.personalizedlearningassistant.backend2.repository;

import com.personalizedlearningassistant.backend2.model.LearningPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPathEntity, Long> {
    List<LearningPathEntity> findByUserProfileUsername(String username);
}
