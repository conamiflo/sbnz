package com.ftn.sbnz.model.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String content;
    private String contentType; 
    private String category;
    @ElementCollection
    @CollectionTable(name = "post_hashtag", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "hashtags")
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