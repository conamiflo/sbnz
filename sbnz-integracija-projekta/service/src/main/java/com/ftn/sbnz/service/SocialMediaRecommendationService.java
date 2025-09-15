package com.ftn.sbnz.service;


import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.Recommendation;
import com.ftn.sbnz.model.models.User;

import java.util.*;
import java.util.stream.Collectors;

public class SocialMediaRecommendationService {
    
    private KieContainer kieContainer;
    
    public SocialMediaRecommendationService() {
        KieServices kieServices = KieServices.Factory.get();
        kieContainer = kieServices.getKieClasspathContainer();
    }
    
    public List<Recommendation> generateRecommendations(List<User> users, List<Post> posts) {
        KieSession kieSession = kieContainer.newKieSession();
        
        try {
            System.out.println("=== SOCIAL MEDIA RECOMMENDATION SYSTEM ===");
            System.out.println("Users: " + users.size() + ", Posts: " + posts.size());
            System.out.println();
            
            // Ubacujemo sve korisnike i objave u radnu memoriju
            for (User user : users) {
                kieSession.insert(user);
                System.out.println("Inserted user: " + user.getName() + " (" + user.getCreatorType() + ")");
            }
            
            for (Post post : posts) {
                kieSession.insert(post);
            }
            
            System.out.println();
            System.out.println("=== FIRING RULES ===");
            
            // Aktiviramo pravila
            int rulesTriggered = kieSession.fireAllRules();
            System.out.println();
            System.out.println("Total rules triggered: " + rulesTriggered);
            System.out.println();
            
            // Prikupljamo preporuke iz radne memorije
            List<Recommendation> recommendations = new ArrayList<>();
            for (Object fact : kieSession.getObjects()) {
                if (fact instanceof Recommendation) {
                    recommendations.add((Recommendation) fact);
                }
            }
            
            // Sortiramo po prioritetu
            recommendations.sort((r1, r2) -> Double.compare(r2.getPriorityScore(), r1.getPriorityScore()));
            
            return recommendations;
            
        } finally {
            kieSession.dispose();
        }
    }
    
    public void displayRecommendations(List<Recommendation> recommendations, List<User> users) {
        System.out.println("=== GENERATED RECOMMENDATIONS ===");
        
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations generated.");
            return;
        }
        
        // Grupišemo po korisniku
        Map<String, List<Recommendation>> recommendationsByUser = recommendations.stream()
                .collect(Collectors.groupingBy(Recommendation::getUserId));
        
        for (Map.Entry<String, List<Recommendation>> entry : recommendationsByUser.entrySet()) {
            String userId = entry.getKey();
            List<Recommendation> userRecommendations = entry.getValue();
            
            User user = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElse(null);
            
            System.out.println();
            System.out.println("📱 RECOMMENDATIONS FOR: " + 
                              (user != null ? user.getName() : "User " + userId));
            System.out.println("   Interests: " + (user != null ? user.getInterests() : "N/A"));
            System.out.println("   Type: " + (user != null ? user.getCreatorType() : "N/A"));
            System.out.println("   Location: " + (user != null ? user.getLocation() : "N/A"));
            System.out.println();
            
            for (int i = 0; i < Math.min(userRecommendations.size(), 5); i++) {
                Recommendation rec = userRecommendations.get(i);
                System.out.printf("   %d. [Score: %.2f] %s - %s%n", 
                                i + 1, rec.getPriorityScore(), rec.getContentType().toUpperCase(), 
                                rec.getCategory());
                System.out.println("      Content: " + rec.getContent());
                System.out.println("      Engagement: " + String.format("%.3f", rec.getPredictedEngagement()));
                if (rec.getRecommendedPublishTime() != null) {
                    System.out.println("      Best time: " + rec.getRecommendedPublishTime().getDayOfWeek() + 
                                     " at " + rec.getRecommendedPublishTime().getHour() + ":00");
                }
                System.out.println("      Reasoning: " + rec.getReasoning());
                System.out.println();
            }
        }
        
        System.out.println("=== SUMMARY ===");
        System.out.println("Total recommendations generated: " + recommendations.size());
        System.out.println("Average priority score: " + 
                          String.format("%.2f", recommendations.stream()
                                  .mapToDouble(Recommendation::getPriorityScore)
                                  .average().orElse(0.0)));
        System.out.println("High priority recommendations (>6.0): " + 
                          recommendations.stream()
                                  .mapToLong(r -> r.getPriorityScore() > 6.0 ? 1 : 0)
                                  .sum());
    }
}