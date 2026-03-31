package com.personalizedlearningassistant.backend2.dto.pythonapis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionResponse {
    private boolean success;
    private Assessment assessment;
    private List<Question> questions;

    public QuestionResponse() {}

    public QuestionResponse(boolean success, Assessment assessment, List<Question> questions) {
        this.success = success;
        this.assessment = assessment;
        this.questions = questions;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Assessment {
        private String username;
        private String programminglanguage;
        private String difficultylevel;
        private int totalquestions;
        private String generatedat;

        public Assessment() {}

        public Assessment(String username, String programminglanguage, String difficultylevel, int totalquestions, String generatedat) {
            this.username = username;
            this.programminglanguage = programminglanguage;
            this.difficultylevel = difficultylevel;
            this.totalquestions = totalquestions;
            this.generatedat = generatedat;
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

        public String getDifficultylevel() {
            return difficultylevel;
        }

        public void setDifficultylevel(String difficultylevel) {
            this.difficultylevel = difficultylevel;
        }

        public int getTotalquestions() {
            return totalquestions;
        }

        public void setTotalquestions(int totalquestions) {
            this.totalquestions = totalquestions;
        }

        public String getGeneratedat() {
            return generatedat;
        }

        public void setGeneratedat(String generatedat) {
            this.generatedat = generatedat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Question {
        private int questionno;
        private int questionid;
        private String question;
        private Map<String, String> options;
        private String correctoption;
        private String correctanswer;

        public Question() {}

        public Question(int questionno, int questionid, String question, Map<String, String> options, String correctoption, String correctanswer) {
            this.questionno = questionno;
            this.questionid = questionid;
            this.question = question;
            this.options = options;
            this.correctoption = correctoption;
            this.correctanswer = correctanswer;
        }

        public int getQuestionno() {
            return questionno;
        }

        public void setQuestionno(int questionno) {
            this.questionno = questionno;
        }

        public int getQuestionid() {
            return questionid;
        }

        public void setQuestionid(int questionid) {
            this.questionid = questionid;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public Map<String, String> getOptions() {
            return options;
        }

        public void setOptions(Map<String, String> options) {
            this.options = options;
        }

        public String getCorrectoption() {
            return correctoption;
        }

        public void setCorrectoption(String correctoption) {
            this.correctoption = correctoption;
        }

        public String getCorrectanswer() {
            return correctanswer;
        }

        public void setCorrectanswer(String correctanswer) {
            this.correctanswer = correctanswer;
        }
    }
}

