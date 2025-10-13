package com.ftn.sbnz.model.dto.response;

import com.ftn.sbnz.model.models.Post;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDTO {
    private Long id;
    private String username;
    private String content;
    private String contentType;
    private String category;
    private List<String> hashtags;
    private LocalDateTime publishTime;

    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.username = post.getUser().getUsername();
        this.content = post.getContent();
        this.contentType = post.getContentType();
        this.category = post.getCategory();
        this.hashtags = post.getHashtags();
        this.publishTime = post.getPublishTime();
    }
}