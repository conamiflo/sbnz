package com.ftn.sbnz.service.controllers;


import com.ftn.sbnz.model.dto.request.RecommendationRequest;
import com.ftn.sbnz.model.dto.response.RecommendationResponse;
import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.SocialMediaRecommendationService;
import com.ftn.sbnz.service.services.TemplateService;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    @Autowired
    private TemplateService templateService;

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

    @PostMapping("/template-generate-from-excel")
    public String generateFromExcel() {
        KieSession session = templateService.generateRulesFromTable();

        // Test user
        User user = new User("ognjen", 22, "Novi Sad", "male", "nzm", 1000);
        user.addInterest("fashion");
        user.addInterest("fitness");

        // Create posts with full constructor
        Post post1 = new Post(
                user,
                "Fashion tips for summer",
                "image",
                "fashion",
                List.of("#style", "#trend", "#outfit"),
                LocalDateTime.now().minusDays(1),
                120,   // likes
                30,    // comments
                15,    // shares
                2000,  // reach
                0.07   // engagementRate
        );

        Post post2 = new Post(
                user,
                "Morning workout routine",
                "video",
                "fitness",
                List.of("#fitness", "#gym", "#motivation"),
                LocalDateTime.now().minusHours(5),
                90,    // likes
                25,    // comments
                10,    // shares
                1800,  // reach
                0.06   // engagementRate
        );

        // Trending hashtags
        TrendingHashtag trend1 = new TrendingHashtag("#style");
        TrendingHashtag trend2 = new TrendingHashtag("#fitness");

        // Insert into session
        session.insert(post1);
        session.insert(post2);
        session.insert(trend1);
        session.insert(trend2);
        session.insert(user);

        int fired = session.fireAllRules();
        session.dispose();

        return "Rules generated from Excel. Fired " + fired + " rules.";
    }




}