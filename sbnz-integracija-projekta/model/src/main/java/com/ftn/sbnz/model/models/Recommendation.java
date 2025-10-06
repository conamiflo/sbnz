package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;

public class Recommendation {
    private String id;
    private Long userId;
    private Long postId;
    private String contentType;
    private String category;
    private String content;
    private double priorityScore;
    private String reasoning;
    private LocalDateTime recommendedPublishTime;
    private double predictedEngagement;
    private String status; 
    
    public Recommendation() {
        this.status = "pending";
    }
    
    public Recommendation(Long userId, Long postId, String contentType, String category, String content) {
        this.userId = userId;
        this.postId = postId;
        this.contentType = contentType;
        this.category = category;
        this.content = content;
        this.status = "pending";
        this.priorityScore = 0.0;
        this.predictedEngagement = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }
    
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    
    public LocalDateTime getRecommendedPublishTime() { return recommendedPublishTime; }
    public void setRecommendedPublishTime(LocalDateTime recommendedPublishTime) { 
        this.recommendedPublishTime = recommendedPublishTime; 
    }
    
    public double getPredictedEngagement() { return predictedEngagement; }
    public void setPredictedEngagement(double predictedEngagement) { 
        this.predictedEngagement = predictedEngagement; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public void increasePriorityScore(double increase, String reason) {
        this.priorityScore += increase;
        if (this.reasoning == null || this.reasoning.isEmpty()) {
            this.reasoning = reason;
        } else {
            this.reasoning += "; " + reason;
        }
    }


    
    @Override
    public String toString() {
        return "Recommendation{" +
                "userId='" + userId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", category='" + category + '\'' +
                ", content='" + content + '\'' +
                ", priorityScore=" + String.format("%.2f", priorityScore) +
                ", predictedEngagement=" + String.format("%.3f", predictedEngagement) +
                ", recommendedTime=" + (recommendedPublishTime != null ? recommendedPublishTime.toString() : "N/A") +
                ", reasoning='" + reasoning + '\'' +
                '}';
    }
}