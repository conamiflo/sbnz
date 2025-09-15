package com.ftn.sbnz.model.dto;

import java.util.List;
import java.util.Map;

import com.ftn.sbnz.model.models.Recommendation;

public class RecommendationResponse {
    
    private boolean success;
    private String message;
    private List<Recommendation> recommendations;
    private Map<String, List<Recommendation>> recommendationsByUser;
    private int totalCount;
    private double averagePriorityScore;
    private long highPriorityCount;
    
    public RecommendationResponse() {}
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
    
    public Map<String, List<Recommendation>> getRecommendationsByUser() {
        return recommendationsByUser;
    }
    
    public void setRecommendationsByUser(Map<String, List<Recommendation>> recommendationsByUser) {
        this.recommendationsByUser = recommendationsByUser;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public double getAveragePriorityScore() {
        return averagePriorityScore;
    }
    
    public void setAveragePriorityScore(double averagePriorityScore) {
        this.averagePriorityScore = averagePriorityScore;
    }
    
    public long getHighPriorityCount() {
        return highPriorityCount;
    }
    
    public void setHighPriorityCount(long highPriorityCount) {
        this.highPriorityCount = highPriorityCount;
    }
}