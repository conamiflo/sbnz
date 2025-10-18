package com.ftn.sbnz.model.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViralMomentumAlert implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private String trendingHashtag;
    private LocalDateTime detectedAt;

    public ViralMomentumAlert(String message, String trendingHashtag) {
        this.message = message;
        this.trendingHashtag = trendingHashtag;
        this.detectedAt = LocalDateTime.now();
    }
}