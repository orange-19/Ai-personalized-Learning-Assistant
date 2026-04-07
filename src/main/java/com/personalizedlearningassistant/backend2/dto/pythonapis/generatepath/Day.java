package com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Day {
    private int day;
    private String topic;
    private List<String> subtopics;
    private String exercise;
    private double estimatedhours;
    private String focusarea;
    private List<String> resources;

    public Day() {}

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(List<String> subtopics) {
        this.subtopics = subtopics;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public double getEstimatedhours() {
        return estimatedhours;
    }

    public void setEstimatedhours(double estimatedhours) {
        this.estimatedhours = estimatedhours;
    }

    public String getFocusarea() {
        return focusarea;
    }

    public void setFocusarea(String focusarea) {
        this.focusarea = focusarea;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}

