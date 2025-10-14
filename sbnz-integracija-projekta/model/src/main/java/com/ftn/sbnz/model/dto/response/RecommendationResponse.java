package com.ftn.sbnz.model.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

    private boolean success;
    private String message;
    private List<RecommendationDTO> recommendations;
    private Map<String, List<RecommendationDTO>> recommendationsByUser;
    private int totalCount;
    private double averagePriorityScore;
    private long highPriorityCount;

}