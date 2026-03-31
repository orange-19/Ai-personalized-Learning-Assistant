package com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneratePathResponse {

    private boolean success;
    private LearningPath learningpath;
    private List<Day> days;

    public GeneratePathResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LearningPath getLearningpath() {
        return learningpath;
    }

    public void setLearningpath(LearningPath learningpath) {
        this.learningpath = learningpath;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

}
