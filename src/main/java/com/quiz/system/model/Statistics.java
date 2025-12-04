package com.quiz.system.model;


public class Statistics {

    private Long id;

    private Integer questionNumber;

    private Long goCount;

    private Long noGoCount;

    public Statistics() {}

    public Statistics(Integer questionNumber) {
        this.questionNumber = questionNumber;
        this.goCount = 0L;
        this.noGoCount = 0L;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoCount() {
        return goCount;
    }

    public void setGoCount(Long goCount) {
        this.goCount = goCount;
    }

    public Long getNoGoCount() {
        return noGoCount;
    }

    public void setNoGoCount(Long noGoCount) {
        this.noGoCount = noGoCount;
    }

    public void incrementGo() { this.goCount++; }
    public void incrementNoGo() { this.noGoCount++; }

}
