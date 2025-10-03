package com.ftn.sbnz.service;


import com.ftn.sbnz.model.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ftn.sbnz.model.dto.RecommendationRequest;
import com.ftn.sbnz.model.dto.RecommendationResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private SocialMediaRecommendationService recommendationService;
    
    @Autowired
    private TemplateService templateService;


    @PostMapping("/visualize")
    public ResponseEntity<String> visualizeRecommendations(@RequestBody RecommendationRequest request) {
        List<Recommendation> recommendations = recommendationService.generateRecommendations(
            request.getUsers(), request.getPosts()
        );

        templateService.fireRecommendations(recommendations);

        return ResponseEntity.ok("Recommendations visualized via Drools template!");
    }

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
            
            Map<String, List<Recommendation>> recommendationsByUser = recommendations.stream()
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
        
        User user1 = new User("user1", "Marko Petrović", 28, "Novi Sad", "M", "influencer", 5000);
        user1.addInterest("fitness");
        user1.addInterest("lifestyle");
        
        User user2 = new User("user2", "Ana Jovanović", 25, "Novi Sad", "F", "personal", 800);
        user2.addInterest("food");
        user2.addInterest("travel");
        
        User user3 = new User("user3", "Stefan Nikolić", 30, "Belgrade", "M", "brand", 15000);
        user3.addInterest("fitness");
        user3.addInterest("technology");
        
        User user4 = new User("user4", "Milica Stojanović", 27, "Novi Sad", "F", "influencer", 3200);
        user4.addInterest("lifestyle");
        user4.addInterest("food");
        
        request.setUsers(List.of(user1, user2, user3, user4));

        
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


}