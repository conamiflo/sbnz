package com.ftn.sbnz.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGoal {
    private String userId;
    private String target; // "engagement", "reach", "conversion", itd.

    @Override
    public String toString() {
        return "UserGoal{" +
                "userId='" + userId + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}