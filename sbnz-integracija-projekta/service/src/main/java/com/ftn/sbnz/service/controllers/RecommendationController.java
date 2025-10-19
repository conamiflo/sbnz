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

        // User who will receive recommendations
        User targetUser = new User("ognjen", 22, "Novi Sad", "male", "influencer", 1000);
        targetUser.setId(1L);
        targetUser.addInterest("fashion");
        targetUser.addInterest("fitness");
        targetUser.addInterest("tech");

        // Different user who owns the posts
        User contentCreator = new User("john_doe", 28, "New York", "male", "creator", 5000);
        contentCreator.setId(2L);

        // Create posts owned by contentCreator (NOT targetUser)
        Post post1 = new Post(
                contentCreator,  // Different user!
                "Fashion tips for summer",
                "image",
                "fashion",
                List.of("#style", "#trend", "#outfit"),
                LocalDateTime.now().minusDays(1),
                120,   // likes
                30,    // comments
                15,    // shares
                2000,  // reach
                0.07   // engagementRate (>= 0.06 required)
        );
        post1.setId(101L);

        Post post2 = new Post(
                contentCreator,  // Different user!
                "Morning workout routine",
                "video",
                "fitness",
                List.of("#fitness", "#gym", "#motivation"),
                LocalDateTime.now().minusHours(5),
                90,    // likes
                25,    // comments
                10,    // shares
                1800,  // reach
                0.06   // engagementRate (>= 0.05 required)
        );
        post2.setId(102L);

        Post post3 = new Post(
                contentCreator,  // Different user!
                "Latest AI trends in 2025",
                "video",
                "tech",
                List.of("#technology", "#AI", "#innovation"),
                LocalDateTime.now().minusHours(3),
                200,   // likes
                50,    // comments
                25,    // shares
                3000,  // reach
                0.09   // engagementRate (>= 0.04 required)
        );
        post3.setId(103L);

        // Trending hashtags
        TrendingHashtag trend1 = new TrendingHashtag("#style",75);
        TrendingHashtag trend2 = new TrendingHashtag("#fitness", 90);
        TrendingHashtag trend3 = new TrendingHashtag("#technology",80);

        // Insert into session - ORDER MATTERS!
        session.insert(targetUser);
        session.insert(contentCreator);
        session.insert(post1);
        session.insert(post2);
        session.insert(post3);
        session.insert(trend1);
        session.insert(trend2);
        session.insert(trend3);

        System.out.println("=== Before firing rules ===");
        System.out.println("Target user: " + targetUser.getName() +
                " (ID: " + targetUser.getId() +
                ", Type: " + targetUser.getCreatorType() + ")");
        System.out.println("Interests: " + targetUser.getInterests());
        System.out.println("Posts inserted: 3 (owned by user ID: " + contentCreator.getId() + ")");

        int fired = session.fireAllRules();

        System.out.println("=== After firing rules ===");
        System.out.println("Rules fired: " + fired);

        // Collect recommendations
        List<Recommendation> recommendations = new ArrayList<>();
        for (Object obj : session.getObjects()) {
            if (obj instanceof Recommendation) {
                recommendations.add((Recommendation) obj);
            }
        }

        System.out.println("Recommendations created: " + recommendations.size());
        for (Recommendation rec : recommendations) {
            System.out.println("  - " + rec.getReasoning() +
                    " | Score: " + rec.getPriorityScore() +
                    " | Category: " + rec.getCategory());
        }

        session.dispose();

        return "Rules generated from Excel. Fired " + fired + " rules. " +
                "Created " + recommendations.size() + " recommendations.";
    }
}