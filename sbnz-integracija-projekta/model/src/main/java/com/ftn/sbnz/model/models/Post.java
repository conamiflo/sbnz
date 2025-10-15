package com.ftn.sbnz.model.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
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

    public Post(User user, String content, String contentType, String category, List<String> hashtags, LocalDateTime publishTime, int likes, int comments, int shares, int reach, double engagementRate) {
        this.user = user;
        this.content = content;
        this.contentType = contentType;
        this.category = category;
        this.hashtags = hashtags;
        this.publishTime = publishTime;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.reach = reach;
        this.engagementRate = engagementRate;
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

}