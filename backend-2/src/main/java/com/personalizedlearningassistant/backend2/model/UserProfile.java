package com.personalizedlearningassistant.backend2.model;

import jakarta.persistence.*;
import java.util.List;

@Entity(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String name;
    private String rollno;
    private String email;
    private String password;
    private String avatarUrl;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningPathEntity> learningPathList;

    public UserProfile() {}

    public UserProfile(String username, String name, String rollno, String email, String password, String avatarUrl) {
        this.username = username;
        this.name = name;
        this.rollno = rollno;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

    public UserProfile(Long id, String username, String name, String rollno, String email, String password, String avatarUrl, List<LearningPathEntity> learningPathList) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.rollno = rollno;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.learningPathList = learningPathList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<LearningPathEntity> getLearningPathList() {
        return learningPathList;
    }

    public void setLearningPathList(List<LearningPathEntity> learningPathList) {
        this.learningPathList = learningPathList;
    }

}
