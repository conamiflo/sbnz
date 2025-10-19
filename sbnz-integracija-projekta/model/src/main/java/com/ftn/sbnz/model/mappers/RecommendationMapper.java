package com.ftn.sbnz.model.mappers;

import com.ftn.sbnz.model.dto.response.RecommendationBackwardDTO;
import com.ftn.sbnz.model.models.Recommendation;

public class RecommendationMapper {
    public static RecommendationBackwardDTO toDto(Recommendation rec) {
        if (rec == null) return null;

        return new RecommendationBackwardDTO(
                rec.getUser() != null ? rec.getUser().getId() : null,
                rec.getUser() != null ? rec.getUser().getName() : null,
                rec.getPost() != null ? rec.getPost().getId() : null,
                rec.getPost() != null ? rec.getPost().getContent() : null,
                rec.getPost() != null ? rec.getPost().getCategory() : null,
                rec.getPost() != null ? rec.getPost().getContentType() : null,
                rec.getPriorityScore(),
                rec.getReasoning(),
                rec.getRecommendedPublishTime(),
                rec.getPredictedEngagement(),
                rec.getStatus()
        );
    }
}
