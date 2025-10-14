package com.ftn.sbnz.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private String content;
    private String reason;
    private double priorityScore;
    private UserDTO user;
}