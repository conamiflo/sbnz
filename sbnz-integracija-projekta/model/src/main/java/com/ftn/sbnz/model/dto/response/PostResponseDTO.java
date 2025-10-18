package com.ftn.sbnz.model.dto.response;

import com.ftn.sbnz.model.models.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PostResponseDTO {

    private Long id;
    private String content;
    private String contentType;
    private String category;
    private List<String> hashtags;
    private LocalDateTime publishTime;
    private int likes;
    private int comments;
    private int shares;
    private int reach;
    private double engagementRate;

    private String username;


    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.contentType = post.getContentType();
        this.category = post.getCategory();
        this.hashtags = post.getHashtags();
        this.publishTime = post.getPublishTime();
        this.likes = post.getLikes();
        this.comments = post.getComments();
        this.shares = post.getShares();
        this.reach = post.getReach();
        this.engagementRate = post.getEngagementRate();

        if (post.getUser() != null) {
            this.username = post.getUser().getUsername();
        } else {
            this.username = "unknown";
        }
    }
}