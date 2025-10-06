package com.ftn.sbnz.model.models;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Recommendation {

    private User user;
    private Post post;
    private String contentType;
    private String category;
    private String content;
    private double priorityScore;
    private String reasoning;
    private LocalDateTime recommendedPublishTime;
    private double predictedEngagement;
    private String status;

    // Podrazumevani konstruktor
    public Recommendation() {
        this.status = "pending";
        this.priorityScore = 0.0;
        this.predictedEngagement = 0.0;
    }


    public Recommendation(User user, Post post) {
        this();
        this.user = user;
        this.post = post;
        this.contentType = post.getContentType();
        this.category = post.getCategory();
        this.content = post.getContent();
    }

    public void increasePriorityScore(double increase, String reason) {
        this.priorityScore += increase;
        if (this.reasoning == null || this.reasoning.isEmpty()) {
            this.reasoning = reason;
        } else {
            this.reasoning += "; " + reason;
        }
    }
}