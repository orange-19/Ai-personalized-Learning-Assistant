package com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateDiagnosticResponse {

    private boolean success;
    private InternalSection internal;
    private FrontendSection frontend;

    public GenerateDiagnosticResponse() {}

    public GenerateDiagnosticResponse(boolean success, InternalSection internal, FrontendSection frontend) {
        this.success = success;
        this.internal = internal;
        this.frontend = frontend;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public InternalSection getInternal() { return internal; }
    public void setInternal(InternalSection internal) { this.internal = internal; }

    public FrontendSection getFrontend() { return frontend; }
    public void setFrontend(FrontendSection frontend) { this.frontend = frontend; }

    // ─── InternalSection ────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalSection {

        private boolean success;
        private Assessment assessment;
        private List<InternalQuestion> questions;

        public InternalSection() {}

        public InternalSection(boolean success, Assessment assessment, List<InternalQuestion> questions) {
            this.success = success;
            this.assessment = assessment;
            this.questions = questions;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public Assessment getAssessment() { return assessment; }
        public void setAssessment(Assessment assessment) { this.assessment = assessment; }

        public List<InternalQuestion> getQuestions() { return questions; }
        public void setQuestions(List<InternalQuestion> questions) { this.questions = questions; }
    }

    // ─── FrontendSection ────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FrontendSection {

        private boolean success;
        private Assessment assessment;
        private List<FrontendQuestion> questions;

        public FrontendSection() {}

        public FrontendSection(boolean success, Assessment assessment, List<FrontendQuestion> questions) {
            this.success = success;
            this.assessment = assessment;
            this.questions = questions;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public Assessment getAssessment() { return assessment; }
        public void setAssessment(Assessment assessment) { this.assessment = assessment; }

        public List<FrontendQuestion> getQuestions() { return questions; }
        public void setQuestions(List<FrontendQuestion> questions) { this.questions = questions; }
    }

    // ─── Assessment ─────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Assessment {

        private String username;

        @JsonProperty("programminglanguage")
        private String programmingLanguage;

        @JsonProperty("totalquestions")
        private int totalQuestions;

        @JsonProperty("generatedat")
        private String generatedAt;

        public Assessment() {}

        public Assessment(String username, String programmingLanguage, int totalQuestions, String generatedAt) {
            this.username = username;
            this.programmingLanguage = programmingLanguage;
            this.totalQuestions = totalQuestions;
            this.generatedAt = generatedAt;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getProgrammingLanguage() { return programmingLanguage; }
        public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }

        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    }

    // ─── InternalQuestion ───────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalQuestion {

        @JsonProperty("questionno")
        private int questionNo;

        @JsonProperty("questionid")
        private int questionId;

        private String topic;
        private String question;
        private Map<String, String> options;

        @JsonProperty("correctoption")
        private String correctOption;

        @JsonProperty("correctanswer")
        private String correctAnswer;

        public InternalQuestion() {}

        public InternalQuestion(int questionNo, int questionId, String topic, String question,
                                Map<String, String> options, String correctOption, String correctAnswer) {
            this.questionNo = questionNo;
            this.questionId = questionId;
            this.topic = topic;
            this.question = question;
            this.options = options;
            this.correctOption = correctOption;
            this.correctAnswer = correctAnswer;
        }

        public int getQuestionNo() { return questionNo; }
        public void setQuestionNo(int questionNo) { this.questionNo = questionNo; }

        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public Map<String, String> getOptions() { return options; }
        public void setOptions(Map<String, String> options) { this.options = options; }

        public String getCorrectOption() { return correctOption; }
        public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    }

    // ─── FrontendQuestion ───────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FrontendQuestion {

        @JsonProperty("questionno")
        private int questionNo;

        @JsonProperty("questionid")
        private int questionId;

        private String topic;
        private String question;
        private Map<String, String> options;

        public FrontendQuestion() {}

        public FrontendQuestion(int questionNo, int questionId, String topic, String question,
                                Map<String, String> options) {
            this.questionNo = questionNo;
            this.questionId = questionId;
            this.topic = topic;
            this.question = question;
            this.options = options;
        }

        public int getQuestionNo() { return questionNo; }
        public void setQuestionNo(int questionNo) { this.questionNo = questionNo; }

        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public Map<String, String> getOptions() { return options; }
        public void setOptions(Map<String, String> options) { this.options = options; }
    }
}