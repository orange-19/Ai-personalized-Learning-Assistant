package com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    // ─── Assessment ─────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Assessment {

        private String username;

        @JsonProperty("programminglanguage")
        private String programmingLanguage;

        @JsonProperty("difficultylevel")
        private String difficultyLevel;

        @JsonProperty("totalquestions")
        private int totalQuestions;

        @JsonProperty("generatedat")
        private String generatedAt;

        public Assessment() {}

        public Assessment(String username, String programmingLanguage, String difficultyLevel,
                         int totalQuestions, String generatedAt) {
            this.username = username;
            this.programmingLanguage = programmingLanguage;
            this.difficultyLevel = difficultyLevel;
            this.totalQuestions = totalQuestions;
            this.generatedAt = generatedAt;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getProgrammingLanguage() { return programmingLanguage; }
        public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }

        public String getDifficultyLevel() { return difficultyLevel; }
        public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    }

    // ─── Question ───────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Question {

        @JsonProperty("questionno")
        private int questionNo;

        @JsonProperty("questionid")
        private int questionId;

        private String question;
        private Map<String, String> options;

        @JsonProperty("correctoption")
        private String correctOption;

        @JsonProperty("correctanswer")
        private String correctAnswer;

        public Question() {}

        public Question(int questionNo, int questionId, String question, Map<String, String> options,
                       String correctOption, String correctAnswer) {
            this.questionNo = questionNo;
            this.questionId = questionId;
            this.question = question;
            this.options = options;
            this.correctOption = correctOption;
            this.correctAnswer = correctAnswer;
        }

        public int getQuestionNo() { return questionNo; }
        public void setQuestionNo(int questionNo) { this.questionNo = questionNo; }

        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public Map<String, String> getOptions() { return options; }
        public void setOptions(Map<String, String> options) { this.options = options; }

        public String getCorrectOption() { return correctOption; }
        public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    }
}

