package com.ftn.sbnz.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngagementHistory {
    private String userId;
    private String category;        // dodato: tip sadržaja ili kategorija
    private double avgLikes;
    private double avgComments;
    private double avgShares;

    @Override
    public String toString() {
        return "EngagementHistory{" +
                "userId='" + userId + '\'' +
                ", category='" + category + '\'' +
                ", avgLikes=" + avgLikes +
                ", avgComments=" + avgComments +
                ", avgShares=" + avgShares +
                '}';
    }
}
