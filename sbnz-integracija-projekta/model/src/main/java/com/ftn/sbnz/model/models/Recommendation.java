package com.ftn.sbnz.model.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    public Recommendation() {
        this.status = "pending";
    }
    public Recommendation(User user, Post post) {
        this();
        this.user = user;
        this.post = post;
        this.contentType = post.getContentType();
        this.category = post.getCategory();
        this.content = post.getContent();
    }
    public Recommendation(Long userId, Long postId, String contentType, String category, String content) {
        this.user = new User();
        this.user.setId(userId);
        this.post = new Post();
        this.post.setId(postId);
        this.contentType = contentType;
        this.category = category;
        this.content = content;
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