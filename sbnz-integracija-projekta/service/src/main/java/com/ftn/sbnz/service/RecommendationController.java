package com.ftn.sbnz.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ftn.sbnz.model.dto.RecommendationRequest;
import com.ftn.sbnz.model.dto.RecommendationResponse;
import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.Recommendation;
import com.ftn.sbnz.model.models.User;

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

        droolsService.fireRecommendations(recommendations);

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
        
        Post post1 = new Post("post1", "user3", "HIIT Summer Workout - 15 minuta do savršene forme!",
                             "video", "fitness");
        post1.addHashtag("#fitness");
        post1.addHashtag("#hiit");
        post1.addHashtag("#summer");
        post1.setLikes(150);
        post1.setComments(25);
        post1.setShares(12);
        post1.setReach(2500);
        post1.calculateEngagementRate();
        
        Post post2 = new Post("post2", "user2", "Najbolji recept za letnju salatu sa avokadom", 
                             "image", "food");
        post2.addHashtag("#healthyfood");
        post2.addHashtag("#avocado");
        post2.addHashtag("#summer");
        post2.setLikes(89);
        post2.setComments(15);
        post2.setShares(8);
        post2.setReach(1800);
        post2.calculateEngagementRate();
        
        Post post3 = new Post("post3", "user4", "Moja jutarnja rutina za produktivan dan", 
                             "video", "lifestyle");
        post3.addHashtag("#lifestyle");
        post3.addHashtag("#morning");
        post3.addHashtag("#routine");
        post3.setLikes(95);
        post3.setComments(18);
        post3.setShares(6);
        post3.setReach(2200);
        post3.calculateEngagementRate();
        
        Post post4 = new Post("post4", "user1", "Beach body transformation - pre i posle", 
                             "image", "fitness");
        post4.addHashtag("#transformation");
        post4.addHashtag("#fitness");
        post4.addHashtag("#beach");
        post4.setLikes(203);
        post4.setComments(31);
        post4.setShares(18);
        post4.setReach(4200);
        post4.calculateEngagementRate();
        
        Post post5 = new Post("post5", "user4", "Top 5 mesta u Novom Sadu za savršenu fotografiju", 
                             "text", "lifestyle");
        post5.addHashtag("#novised");
        post5.addHashtag("#photography");
        post5.addHashtag("#lifestyle");
        post5.setLikes(67);
        post5.setComments(12);
        post5.setShares(4);
        post5.setReach(1500);
        post5.calculateEngagementRate();
        
        request.setPosts(List.of(post1, post2, post3, post4, post5));
        
        return request;
    }
}