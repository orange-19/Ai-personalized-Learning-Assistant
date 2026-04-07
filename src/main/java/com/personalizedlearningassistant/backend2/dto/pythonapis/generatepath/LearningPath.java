package com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LearningPath {
    private String username;
    private String programminglanguage;
    private String skilllevel;
    private double diagnosticscore;
    private List<String> weaktopics;
    private List<String> strongtopics;
    private int totaldays;
    private double dailyhourstostudy;
    private String goal;
    private String generatedat;

    public LearningPath() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProgramminglanguage() {
        return programminglanguage;
    }

    public void setProgramminglanguage(String programminglanguage) {
        this.programminglanguage = programminglanguage;
    }

    public String getSkilllevel() {
        return skilllevel;
    }

    public void setSkilllevel(String skilllevel) {
        this.skilllevel = skilllevel;
    }

    public double getDiagnosticscore() {
        return diagnosticscore;
    }

    public void setDiagnosticscore(double diagnosticscore) {
        this.diagnosticscore = diagnosticscore;
    }

    public List<String> getWeaktopics() {
        return weaktopics;
    }

    public void setWeaktopics(List<String> weaktopics) {
        this.weaktopics = weaktopics;
    }

    public List<String> getStrongtopics() {
        return strongtopics;
    }

    public void setStrongtopics(List<String> strongtopics) {
        this.strongtopics = strongtopics;
    }

    public int getTotaldays() {
        return totaldays;
    }

    public void setTotaldays(int totaldays) {
        this.totaldays = totaldays;
    }

    public double getDailyhourstostudy() {
        return dailyhourstostudy;
    }

    public void setDailyhourstostudy(double dailyhourstostudy) {
        this.dailyhourstostudy = dailyhourstostudy;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getGeneratedat() {
        return generatedat;
    }

    public void setGeneratedat(String generatedat) {
        this.generatedat = generatedat;
    }
}

