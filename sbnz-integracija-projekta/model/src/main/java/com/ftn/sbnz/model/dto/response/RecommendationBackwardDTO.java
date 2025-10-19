package com.ftn.sbnz.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationBackwardDTO {
    private Long userId;
    private String userName;

    private Long postId;
    private String postContent;
    private String postCategory;
    private String postContentType;

    private double priorityScore;
    private String reasoning;
    private LocalDateTime recommendedPublishTime;
    private double predictedEngagement;
    private String status;
}
