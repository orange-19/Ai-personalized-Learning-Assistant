package com.personalizedlearningassistant.backend2.dto.profiledtos;

public class Profiledto {
    private String username;
    private String name;
    private String rollno;
    private String email;
    private String password;
    private String avatarUrl;

    public Profiledto(String username, String name, String rollno, String email, String password, String avatarUrl) {
        this.username = username;
        this.name = name;
        this.rollno = rollno;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }


    public Profiledto() {}


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

}
