package com.personalizedlearningassistant.backend2.dto.pythonapis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class EvaluateRequest {
    private String username;
    private String programminglanguage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Answer{
        private int questionid;
        private String chosenoption;

        public Answer(){}

        public Answer(int questionid, String chosenoption) {
            this.questionid = questionid;
            this.chosenoption = chosenoption;
        }

        public int getQuestionid() {
            return questionid;
        }

        public void setQuestionid(int questionid) {
            this.questionid = questionid;
        }

        public String getChosenoption() {
            return chosenoption;
        }

        public void setChosenoption(String chosenoption) {
            this.chosenoption = chosenoption;
        }
    }
    private Answer[] answers;

    EvaluateRequest(){}

    public EvaluateRequest(String username, String programminglanguage, Answer[] answers) {
        this.username = username;
        this.programminglanguage = programminglanguage;
        this.answers = answers;
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

    public Answer[] getAnswers() {
        return answers;
    }

    public void setAnswers(Answer[] answers) {
        this.answers = answers;
    }
}
