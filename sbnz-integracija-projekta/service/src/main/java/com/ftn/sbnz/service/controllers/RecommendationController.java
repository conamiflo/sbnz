package com.ftn.sbnz.service.controllers;


import com.ftn.sbnz.model.dto.request.RecommendationRequest;
import com.ftn.sbnz.model.dto.response.RecommendationResponse;
import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.SocialMediaRecommendationService;
import com.ftn.sbnz.service.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final SocialMediaRecommendationService recommendationService;
    private final UserRepository userRepository;

    @Autowired
    public RecommendationController(SocialMediaRecommendationService recommendationService, UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/generate")
    public ResponseEntity<RecommendationResponse> generateRecommendations() {
        try {
            RecommendationResponse response = recommendationService.generateAndPackageRecommendations();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RecommendationResponse errorResponse = new RecommendationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error generating recommendations: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping("/cep/analyze-trends/{userId}")
    public ResponseEntity<?> analyzeTrendsForUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<RelevantTrendAlert> alerts = recommendationService.detectAndAnalyzeRelevantTrends(user);
        if (alerts.isEmpty()) {
            return ResponseEntity.ok("No new relevant trends detected for user " + user.getName());
        }
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/cep/check-saturation")
    public ResponseEntity<?> checkAudienceSaturation() {
        Optional<AudienceSaturationAlert> alert = recommendationService.detectAudienceSaturation();
        if (alert.isPresent()) {
            return ResponseEntity.ok(alert.get());
        }
        return ResponseEntity.ok("No audience saturation detected.");
    }


}