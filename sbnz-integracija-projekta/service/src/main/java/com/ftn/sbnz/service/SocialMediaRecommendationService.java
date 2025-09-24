package com.ftn.sbnz.service;

import com.ftn.sbnz.model.events.EngagementEvent;
import com.ftn.sbnz.model.events.HashtagUsageEvent;
import com.ftn.sbnz.model.models.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SocialMediaRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(SocialMediaRecommendationService.class);

    private final KieContainer kieContainer;

    @Autowired
    public SocialMediaRecommendationService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public List<Recommendation> generateRecommendations(List<User> users, List<Post> posts) {
        KieSession kieSession = null;

        try {

            if (kieContainer != null) {
                try {
                    kieSession = kieContainer.newKieSession("fwKsession");
                    log.info("Using fwKsession for forward package rules");
                } catch (Exception e) {
                    log.warn("fwKsession not found, trying default session: " + e.getMessage());
                    kieSession = kieContainer.newKieSession();
                }
            } else {
                log.warn("No KieContainer injected, using classpath container");
                KieServices kieServices = KieServices.Factory.get();
                KieContainer fallbackContainer = kieServices.getKieClasspathContainer();
                kieSession = fallbackContainer.newKieSession();
            }

            System.out.println("=== SOCIAL MEDIA RECOMMENDATION SYSTEM ===");
            System.out.println("Users: " + users.size() + ", Posts: " + posts.size());
            System.out.println();

            for (User user : users) {
                kieSession.insert(user);
                System.out.println("Inserted user: " + user.getName() + " (" + user.getCreatorType() + ")");
            }

            for (Post post : posts) {
                kieSession.insert(post);
                System.out.println("Inserted post: " + post.getId() + " - " + post.getCategory());
            }

            System.out.println();
            System.out.println("=== FIRING RULES ===");

            int rulesTriggered = kieSession.fireAllRules();
            System.out.println();
            System.out.println("Total rules triggered: " + rulesTriggered);
            System.out.println();

            List<Recommendation> recommendations = new ArrayList<>();
            for (Object fact : kieSession.getObjects()) {
                if (fact instanceof Recommendation) {
                    recommendations.add((Recommendation) fact);
                }
            }

            System.out.println("Found " + recommendations.size() + " recommendations in working memory");

            recommendations.sort((r1, r2) -> Double.compare(r2.getPriorityScore(), r1.getPriorityScore()));

            return recommendations;

        } catch (Exception e) {
            log.error("Error during rule execution: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate recommendations: " + e.getMessage(), e);

        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    public void displayRecommendations(List<Recommendation> recommendations, List<User> users) {
        System.out.println("=== GENERATED RECOMMENDATIONS ===");

        if (recommendations.isEmpty()) {
            System.out.println("No recommendations generated.");
            return;
        }

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

    public List<TrendingHashtag> detectTrendingHashtags() {
        KieSession kieSession = kieContainer.newKieSession("cepKsession");
        SessionPseudoClock clock = kieSession.getSessionClock();
        List<TrendingHashtag> trendingList = new ArrayList<>();


        for (int i = 0; i < 14; i++) {
            kieSession.insert(new HashtagUsageEvent("#fitness"));
            clock.advanceTime(12, TimeUnit.HOURS);
        }

        for (int i = 0; i < 40; i++) {
            kieSession.insert(new HashtagUsageEvent("#fitness"));
            clock.advanceTime(9, TimeUnit.MINUTES);
        }

        kieSession.fireAllRules();

        for (Object fact : kieSession.getObjects()) {
            if (fact instanceof TrendingHashtag) {
                trendingList.add((TrendingHashtag) fact);
            }
        }

        kieSession.dispose();
        return trendingList;
    }


    public EngagementDropAlert detectEngagementDrop() {
        KieSession kieSession = kieContainer.newKieSession("cepKsession");
        SessionPseudoClock clock = kieSession.getSessionClock();


        for (int i = 0; i < 50; i++) {
            double goodEngagement = 0.04 + (Math.random() * 0.02);
            kieSession.insert(new EngagementEvent(goodEngagement));
        }

        clock.advanceTime(3, TimeUnit.DAYS);

        for (int i = 0; i < 10; i++) {
            double badEngagement = 0.01 + (Math.random() * 0.02);
            kieSession.insert(new EngagementEvent(badEngagement));
            clock.advanceTime(1, TimeUnit.HOURS);
        }

        kieSession.fireAllRules();

        EngagementDropAlert alert = null;
        for (Object fact : kieSession.getObjects()) {
            if (fact instanceof EngagementDropAlert) {
                alert = (EngagementDropAlert) fact;
                break;
            }
        }

        kieSession.dispose();
        return alert;
    }

}