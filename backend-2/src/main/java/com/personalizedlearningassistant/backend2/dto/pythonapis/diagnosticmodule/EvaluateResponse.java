package com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluateResponse {

    private boolean success;
    private Evaluation evaluation;
    private List<QuestionResult> wrongquestions;
    private List<QuestionResult> allresults;

    public EvaluateResponse() {}

    public EvaluateResponse(boolean success, Evaluation evaluation, List<QuestionResult> wrongquestions,
                           List<QuestionResult> allresults) {
        this.success = success;
        this.evaluation = evaluation;
        this.wrongquestions = wrongquestions;
        this.allresults = allresults;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Evaluation getEvaluation() { return evaluation; }
    public void setEvaluation(Evaluation evaluation) { this.evaluation = evaluation; }

    public List<QuestionResult> getWrongquestions() { return wrongquestions; }
    public void setWrongquestions(List<QuestionResult> wrongquestions) { this.wrongquestions = wrongquestions; }

    public List<QuestionResult> getAllresults() { return allresults; }
    public void setAllresults(List<QuestionResult> allresults) { this.allresults = allresults; }

    // ─── Evaluation ──────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Evaluation {

        private String username;

        @JsonProperty("programminglanguage")
        private String programmingLanguage;

        @JsonProperty("evaluatedat")
        private String evaluatedAt;

        @JsonProperty("totalquestions")
        private int totalQuestions;

        @JsonProperty("correctcount")
        private int correctCount;

        @JsonProperty("wrongcount")
        private int wrongCount;

        private double score;

        @JsonProperty("skilllevel")
        private String skillLevel;

        @JsonProperty("weaktopics")
        private List<String> weakTopics;

        @JsonProperty("strongtopics")
        private List<String> strongTopics;

        public Evaluation() {}

        public Evaluation(String username, String programmingLanguage, String evaluatedAt, int totalQuestions,
                         int correctCount, int wrongCount, double score, String skillLevel,
                         List<String> weakTopics, List<String> strongTopics) {
            this.username = username;
            this.programmingLanguage = programmingLanguage;
            this.evaluatedAt = evaluatedAt;
            this.totalQuestions = totalQuestions;
            this.correctCount = correctCount;
            this.wrongCount = wrongCount;
            this.score = score;
            this.skillLevel = skillLevel;
            this.weakTopics = weakTopics;
            this.strongTopics = strongTopics;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getProgrammingLanguage() { return programmingLanguage; }
        public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }

        public String getEvaluatedAt() { return evaluatedAt; }
        public void setEvaluatedAt(String evaluatedAt) { this.evaluatedAt = evaluatedAt; }

        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

        public int getCorrectCount() { return correctCount; }
        public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

        public int getWrongCount() { return wrongCount; }
        public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }

        public String getSkillLevel() { return skillLevel; }
        public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

        public List<String> getWeakTopics() { return weakTopics; }
        public void setWeakTopics(List<String> weakTopics) { this.weakTopics = weakTopics; }

        public List<String> getStrongTopics() { return strongTopics; }
        public void setStrongTopics(List<String> strongTopics) { this.strongTopics = strongTopics; }
    }

    // ─── QuestionResult ──────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QuestionResult {

        @JsonProperty("questionno")
        private int questionNo;

        @JsonProperty("questionid")
        private int questionId;

        private String topic;
        private String question;

        @JsonProperty("chosenoption")
        private String chosenOption;

        @JsonProperty("correctoption")
        private String correctOption;

        @JsonProperty("correctanswer")
        private String correctAnswer;

        @JsonProperty("iscorrect")
        private boolean isCorrect;

        public QuestionResult() {}

        public QuestionResult(int questionNo, int questionId, String topic, String question,
                             String chosenOption, String correctOption, String correctAnswer, boolean isCorrect) {
            this.questionNo = questionNo;
            this.questionId = questionId;
            this.topic = topic;
            this.question = question;
            this.chosenOption = chosenOption;
            this.correctOption = correctOption;
            this.correctAnswer = correctAnswer;
            this.isCorrect = isCorrect;
        }

        public int getQuestionNo() { return questionNo; }
        public void setQuestionNo(int questionNo) { this.questionNo = questionNo; }

        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getChosenOption() { return chosenOption; }
        public void setChosenOption(String chosenOption) { this.chosenOption = chosenOption; }

        public String getCorrectOption() { return correctOption; }
        public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }
}

