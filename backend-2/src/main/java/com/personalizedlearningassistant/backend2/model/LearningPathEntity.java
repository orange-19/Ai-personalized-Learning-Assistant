package com.personalizedlearningassistant.backend2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

@Entity(name = "learning_path")
public class LearningPathEntity {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long LearningPathId;

    // Whether the generation succeeded
    private boolean success;

    // Store learningpath JSON as text (could be a separate entity if you want normalized structure)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String learningpathJson;

    // Store days JSON array as text
    @Lob
    @Column(columnDefinition = "TEXT")
    private String daysJson;

    // Many learning paths can belong to one user. This creates a foreign key column user_profile_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private UserProfile userProfile;

    public LearningPathEntity() {}

    public LearningPathEntity(boolean success, String learningpathJson, String daysJson, UserProfile userProfile) {
        this.success = success;
        this.learningpathJson = learningpathJson;
        this.daysJson = daysJson;
        this.userProfile = userProfile;
    }

    public Long getId() {
        return LearningPathId;
    }

    public void setId(Long LearningPathId) {
        this.LearningPathId = LearningPathId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLearningpathJson() {
        return learningpathJson;
    }

    public void setLearningpathJson(String learningpathJson) {
        this.learningpathJson = learningpathJson;
    }

    public String getDaysJson() {
        return daysJson;
    }

    public void setDaysJson(String daysJson) {
        this.daysJson = daysJson;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
