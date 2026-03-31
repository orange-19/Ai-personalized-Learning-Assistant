package com.personalizedlearningassistant.backend2.dto.pythonapis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluateResponse {
    private boolean success;
    private Evaluation evaluation;
    private List<QuestionResult> wrongquestions;
    private List<QuestionResult> allresults;

    public EvaluateResponse() {}

    public EvaluateResponse(boolean success, Evaluation evaluation, List<QuestionResult> wrongquestions, List<QuestionResult> allresults) {
        this.success = success;
        this.evaluation = evaluation;
        this.wrongquestions = wrongquestions;
        this.allresults = allresults;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public List<QuestionResult> getWrongquestions() {
        return wrongquestions;
    }

    public void setWrongquestions(List<QuestionResult> wrongquestions) {
        this.wrongquestions = wrongquestions;
    }

    public List<QuestionResult> getAllresults() {
        return allresults;
    }

    public void setAllresults(List<QuestionResult> allresults) {
        this.allresults = allresults;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Evaluation {
        private String username;
        private String programminglanguage;
        private String evaluatedat;
        private int totalquestions;
        private int correctcount;
        private int wrongcount;
        private double score;
        private String skilllevel;
        private List<String> weaktopics;
        private List<String> strongtopics;

        public Evaluation() {}

        public Evaluation(String username, String programminglanguage, String evaluatedat, int totalquestions, int correctcount, int wrongcount, double score, String skilllevel, List<String> weaktopics, List<String> strongtopics) {
            this.username = username;
            this.programminglanguage = programminglanguage;
            this.evaluatedat = evaluatedat;
            this.totalquestions = totalquestions;
            this.correctcount = correctcount;
            this.wrongcount = wrongcount;
            this.score = score;
            this.skilllevel = skilllevel;
            this.weaktopics = weaktopics;
            this.strongtopics = strongtopics;
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

        public String getEvaluatedat() {
            return evaluatedat;
        }

        public void setEvaluatedat(String evaluatedat) {
            this.evaluatedat = evaluatedat;
        }

        public int getTotalquestions() {
            return totalquestions;
        }

        public void setTotalquestions(int totalquestions) {
            this.totalquestions = totalquestions;
        }

        public int getCorrectcount() {
            return correctcount;
        }

        public void setCorrectcount(int correctcount) {
            this.correctcount = correctcount;
        }

        public int getWrongcount() {
            return wrongcount;
        }

        public void setWrongcount(int wrongcount) {
            this.wrongcount = wrongcount;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getSkilllevel() {
            return skilllevel;
        }

        public void setSkilllevel(String skilllevel) {
            this.skilllevel = skilllevel;
        }

        public List<String> getWeaktopics() {
            return weaktopics;
        }

        public void setWeaktopics(List<String> weaktopics) {
            this.weaktopics = weaktopics;
        }

        public List<String> getStrongtopics() {
            return strongtopics;
        }

        public void setStrongtopics(List<String> strongtopics) {
            this.strongtopics = strongtopics;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QuestionResult {
        private int questionno;
        private int questionid;
        private String topic;
        private String question;
        private String chosenoption;
        private String correctoption;
        private String correctanswer;
        private boolean iscorrect;

        public QuestionResult() {}

        public QuestionResult(int questionno, int questionid, String topic, String question, String chosenoption, String correctoption, String correctanswer, boolean iscorrect) {
            this.questionno = questionno;
            this.questionid = questionid;
            this.topic = topic;
            this.question = question;
            this.chosenoption = chosenoption;
            this.correctoption = correctoption;
            this.correctanswer = correctanswer;
            this.iscorrect = iscorrect;
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

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getChosenoption() {
            return chosenoption;
        }

        public void setChosenoption(String chosenoption) {
            this.chosenoption = chosenoption;
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

        public boolean isIscorrect() {
            return iscorrect;
        }

        public void setIscorrect(boolean iscorrect) {
            this.iscorrect = iscorrect;
        }
    }
}

