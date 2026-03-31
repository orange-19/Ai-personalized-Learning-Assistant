package com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneratePathResponse {

    private boolean success;
    private LearningPath learningpath;
    private List<Day> days;

    public GeneratePathResponse() {}

    public GeneratePathResponse(boolean success, LearningPath learningpath, List<Day> days) {
        this.success = success;
        this.learningpath = learningpath;
        this.days = days;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public LearningPath getLearningpath() { return learningpath; }
    public void setLearningpath(LearningPath learningpath) { this.learningpath = learningpath; }

    public List<Day> getDays() { return days; }
    public void setDays(List<Day> days) { this.days = days; }

    // ─── LearningPath ───────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LearningPath {

        private String username;

        @JsonProperty("programminglanguage")
        private String programmingLanguage;

        @JsonProperty("skilllevel")
        private String skillLevel;

        @JsonProperty("diagnosticscore")
        private double diagnosticScore;

        @JsonProperty("weaktopics")
        private List<String> weakTopics;

        @JsonProperty("strongtopics")
        private List<String> strongTopics;

        @JsonProperty("totaldays")
        private int totalDays;

        @JsonProperty("dailyhourstostudy")
        private double dailyHoursToStudy;

        private String goal;

        @JsonProperty("generatedat")
        private String generatedAt;

        public LearningPath() {}

        public LearningPath(String username, String programmingLanguage, String skillLevel, double diagnosticScore,
                           List<String> weakTopics, List<String> strongTopics, int totalDays,
                           double dailyHoursToStudy, String goal, String generatedAt) {
            this.username = username;
            this.programmingLanguage = programmingLanguage;
            this.skillLevel = skillLevel;
            this.diagnosticScore = diagnosticScore;
            this.weakTopics = weakTopics;
            this.strongTopics = strongTopics;
            this.totalDays = totalDays;
            this.dailyHoursToStudy = dailyHoursToStudy;
            this.goal = goal;
            this.generatedAt = generatedAt;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getProgrammingLanguage() { return programmingLanguage; }
        public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }

        public String getSkillLevel() { return skillLevel; }
        public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

        public double getDiagnosticScore() { return diagnosticScore; }
        public void setDiagnosticScore(double diagnosticScore) { this.diagnosticScore = diagnosticScore; }

        public List<String> getWeakTopics() { return weakTopics; }
        public void setWeakTopics(List<String> weakTopics) { this.weakTopics = weakTopics; }

        public List<String> getStrongTopics() { return strongTopics; }
        public void setStrongTopics(List<String> strongTopics) { this.strongTopics = strongTopics; }

        public int getTotalDays() { return totalDays; }
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

        public double getDailyHoursToStudy() { return dailyHoursToStudy; }
        public void setDailyHoursToStudy(double dailyHoursToStudy) { this.dailyHoursToStudy = dailyHoursToStudy; }

        public String getGoal() { return goal; }
        public void setGoal(String goal) { this.goal = goal; }

        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    }

    // ─── Day ────────────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day {

        private int day;
        private String topic;
        private List<String> subtopics;
        private String exercise;

        @JsonProperty("estimatedhours")
        private double estimatedHours;

        @JsonProperty("focusarea")
        private String focusArea;

        private List<String> resources;

        public Day() {}

        public Day(int day, String topic, List<String> subtopics, String exercise, double estimatedHours,
                  String focusArea, List<String> resources) {
            this.day = day;
            this.topic = topic;
            this.subtopics = subtopics;
            this.exercise = exercise;
            this.estimatedHours = estimatedHours;
            this.focusArea = focusArea;
            this.resources = resources;
        }

        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }

        public List<String> getSubtopics() { return subtopics; }
        public void setSubtopics(List<String> subtopics) { this.subtopics = subtopics; }

        public String getExercise() { return exercise; }
        public void setExercise(String exercise) { this.exercise = exercise; }

        public double getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }

        public String getFocusArea() { return focusArea; }
        public void setFocusArea(String focusArea) { this.focusArea = focusArea; }

        public List<String> getResources() { return resources; }
        public void setResources(List<String> resources) { this.resources = resources; }
    }
}

