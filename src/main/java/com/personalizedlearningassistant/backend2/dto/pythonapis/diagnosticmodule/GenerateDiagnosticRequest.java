package com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateDiagnosticRequest {
    private String username;
    private String programminglanguage;
    private Integer days;
    private String goal;

    @JsonProperty("dailyhourstostudy")
    private Integer dailyhourstostudy;

    @JsonProperty("diagnosticquestions")
    private Integer diagnosticquestions;

    GenerateDiagnosticRequest(){}

    public GenerateDiagnosticRequest(String username, String programminglanguage, Integer days, String goal, Integer dailyhourstostudy, Integer diagnosticquestions) {
        this.username = username;
        this.programminglanguage = programminglanguage;
        this.days = days;
        this.goal = goal;
        this.dailyhourstostudy = dailyhourstostudy;
        this.diagnosticquestions = diagnosticquestions;
    }

    public Integer getDailyhourstostudy() {
        return dailyhourstostudy;
    }

    public void setDailyhourstostudy(Integer dailyhourstostudy) {
        this.dailyhourstostudy = dailyhourstostudy;
    }

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

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getDiagnosticquestions() {
        return diagnosticquestions;
    }

    public void setDiagnosticquestions(Integer diagnosticquestions) {
        this.diagnosticquestions = diagnosticquestions;
    }

}
