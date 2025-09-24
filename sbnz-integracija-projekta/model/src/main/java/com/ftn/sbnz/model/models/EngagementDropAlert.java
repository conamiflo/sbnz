package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class EngagementDropAlert implements Serializable {

    private static final long serialVersionUID = 1L;
    private String message;
    private double recentAverage;
    private double baselineAverage;

    public EngagementDropAlert(String message, double recentAverage, double baselineAverage) {
        this.message = message;
        this.recentAverage = recentAverage;
        this.baselineAverage = baselineAverage;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getRecentAverage() {
        return recentAverage;
    }

    public void setRecentAverage(double recentAverage) {
        this.recentAverage = recentAverage;
    }

    public double getBaselineAverage() {
        return baselineAverage;
    }

    public void setBaselineAverage(double baselineAverage) {
        this.baselineAverage = baselineAverage;
    }
}