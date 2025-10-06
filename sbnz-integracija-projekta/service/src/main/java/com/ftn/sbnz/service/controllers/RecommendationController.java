package com.ftn.sbnz.service.controllers;


import com.ftn.sbnz.model.dto.request.RecommendationRequest;
import com.ftn.sbnz.model.dto.response.RecommendationResponse;
import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.service.services.SocialMediaRecommendationService;
import com.ftn.sbnz.service.services.TemplateService;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    
    @Autowired
    private SocialMediaRecommendationService recommendationService;
    
    @Autowired
    private TemplateService templateService;


//    @PostMapping("/visualize")
//    public ResponseEntity<String> visualizeRecommendations(@RequestBody RecommendationRequest request) {
//        List<Recommendation> recommendations = recommendationService.generateRecommendations(
//            request.getUsers(), request.getPosts()
//        );
//
//        templateService.fireRecommendations(recommendations);
//
//        return ResponseEntity.ok("Recommendations visualized via Drools template!");
//    }

    @PostMapping("/generate")
    public ResponseEntity<RecommendationResponse> generateRecommendations(
            @RequestBody RecommendationRequest request) {
        
        try {
            List<Recommendation> recommendations = recommendationService
                    .generateRecommendations(request.getUsers(), request.getPosts());
            
            RecommendationResponse response = new RecommendationResponse();
            response.setRecommendations(recommendations);
            response.setTotalCount(recommendations.size());
            response.setSuccess(true);
            response.setMessage("Successfully generated " + recommendations.size() + " recommendations");
            
            Map<Long, List<Recommendation>> recommendationsByUser = recommendations.stream()
                    .collect(Collectors.groupingBy(Recommendation::getUserId));
            response.setRecommendationsByUser(recommendationsByUser);
            
            double avgScore = recommendations.stream()
                    .mapToDouble(Recommendation::getPriorityScore)
                    .average().orElse(0.0);
            response.setAveragePriorityScore(avgScore);
            
            long highPriorityCount = recommendations.stream()
                    .mapToLong(r -> r.getPriorityScore() > 6.0 ? 1 : 0)
                    .sum();
            response.setHighPriorityCount(highPriorityCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            RecommendationResponse errorResponse = new RecommendationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error generating recommendations: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/demo")
    public ResponseEntity<RecommendationResponse> getDemoRecommendations() {
        RecommendationRequest demoRequest = createDemoData();
        return generateRecommendations(demoRequest);
    }
    

    private RecommendationRequest createDemoData() {
        RecommendationRequest request = new RecommendationRequest();
        return request;
    }

    @GetMapping("/cep-demo")
    public ResponseEntity<List<TrendingHashtag>> getTrendingHashtags() {
        try {
            List<TrendingHashtag> trending = recommendationService.detectTrendingHashtags();
            return ResponseEntity.ok(trending);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/engagement-drop-demo")
    public ResponseEntity<EngagementDropAlert> getEngagementDrop() {
        try {
            EngagementDropAlert alert = recommendationService.detectEngagementDrop();
            if (alert != null) {
                return ResponseEntity.ok(alert);
            }
            return ResponseEntity.ok(new EngagementDropAlert("No significant engagement drop detected.", 0, 0));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    @PostMapping("/template-generate-from-excel")
    public String generateFromExcel() {
        KieSession session = templateService.generateRulesFromTable();

        // Test data
        User user = new User("ognjen", 22, "Novi Sad", "male", "nzm", 1000);
        user.addInterest("fashion");
        user.addInterest("fitness");

        Post post1 = new Post(1L, "user2", "Fashion tips", "image", "fashion");
        post1.setEngagementRate(0.07);

        Post post2 = new Post(2L, "user3", "Workout routine", "video", "fitness");
        post2.setEngagementRate(0.06);

        TrendingHashtag trend1 = new TrendingHashtag("#style");
        TrendingHashtag trend2 = new TrendingHashtag("#fitness");

        session.insert(user);
        session.insert(post1);
        session.insert(post2);
        session.insert(trend1);
        session.insert(trend2);

        int fired = session.fireAllRules();
        session.dispose();

        return "Rules generated from Excel. Fired " + fired + " rules.";
    }


}