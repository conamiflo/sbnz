package com.ftn.sbnz.model.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Post {
    @Id
    private Long id;
    private String userId;
    private String content;
    private String contentType; 
    private String category;
    @ElementCollection
    private List<String> hashtags;
    private LocalDateTime publishTime;
    private int likes;
    private int comments;
    private int shares;
    private int reach;
    private double engagementRate;
    
    public Post() {
        this.hashtags = new ArrayList<>();
        this.publishTime = LocalDateTime.now();
    }
    
    public Post(Long id, String userId, String content, String contentType, String category) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.contentType = contentType;
        this.category = category;
        this.hashtags = new ArrayList<>();
        this.publishTime = LocalDateTime.now();
    }

    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }
    
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    
    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }
    
    public int getShares() { return shares; }
    public void setShares(int shares) { this.shares = shares; }
    
    public int getReach() { return reach; }
    public void setReach(int reach) { this.reach = reach; }
    
    public double getEngagementRate() { return engagementRate; }
    public void setEngagementRate(double engagementRate) { this.engagementRate = engagementRate; }
    
    public void addHashtag(String hashtag) {
        this.hashtags.add(hashtag);
    }
    
    public void calculateEngagementRate() {
        if (reach > 0) {
            this.engagementRate = (double)(likes + comments + shares) / reach;
        }
    }
    
    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", category='" + category + '\'' +
                ", hashtags=" + hashtags +
                ", engagementRate=" + engagementRate +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}