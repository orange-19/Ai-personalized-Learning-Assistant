package com.personalizedlearningassistant.backend2.dto.pythonapis;

public class GenerateQuestionRequest {
    private String username;
    private String programminglanguage;
    private String difficultylevel;
    private int questionneededforassessment;

    GenerateQuestionRequest(){}

    public GenerateQuestionRequest(String username, String programminglanguage, String difficultylevel, int questionneededforassessment) {
        this.username = username;
        this.programminglanguage = programminglanguage;
        this.difficultylevel = difficultylevel;
        this.questionneededforassessment = questionneededforassessment;
    }

    public String getDifficultylevel() {
        return difficultylevel;
    }

    public void setDifficultylevel(String difficultylevel) {
        this.difficultylevel = difficultylevel;
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

    public int getQuestionneededforassessment() {
        return questionneededforassessment;
    }

    public void setQuestionneededforassessment(int questionneededforassessment) {
        this.questionneededforassessment = questionneededforassessment;
    }

}
