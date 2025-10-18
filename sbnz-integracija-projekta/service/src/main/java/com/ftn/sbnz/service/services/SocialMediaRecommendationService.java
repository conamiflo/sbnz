package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.dto.response.RecommendationDTO;
import com.ftn.sbnz.model.dto.response.RecommendationResponse;
import com.ftn.sbnz.model.dto.response.UserDTO;
import com.ftn.sbnz.model.events.EngagementEvent;
import com.ftn.sbnz.model.events.HashtagUsageEvent;
import com.ftn.sbnz.model.events.PostPublishedEvent;
import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.service.repositories.PostRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SocialMediaRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(SocialMediaRecommendationService.class);

    private final KieContainer kieContainer;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public SocialMediaRecommendationService(KieContainer kieContainer, UserRepository userRepository, PostRepository postRepository) {
        this.kieContainer = kieContainer;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public RecommendationResponse generateAndPackageRecommendations() {
        List<Recommendation> recommendationEntities = this.generateRecommendationsInternal();

        List<RecommendationDTO> recommendationDTOs = recommendationEntities.stream()
                .map(entity -> {
                    User userEntity = entity.getUser();
                    UserDTO userDTO = (userEntity != null)
                            ? new UserDTO(userEntity.getId(), userEntity.getName())
                            : null;

                    return new RecommendationDTO(
                            entity.getContent(),
                            entity.getReasoning(),
                            entity.getPriorityScore(),
                            userDTO
                    );
                })
                .collect(Collectors.toList());

        RecommendationResponse response = new RecommendationResponse();
        response.setSuccess(true);
        response.setRecommendations(recommendationDTOs);
        response.setTotalCount(recommendationDTOs.size());
        response.setMessage("Successfully generated " + recommendationDTOs.size() + " recommendations");

        Map<String, List<RecommendationDTO>> recommendationsByUser = recommendationDTOs.stream()
                .filter(r -> r.getUser() != null && r.getUser().getId() != null)
                .collect(Collectors.groupingBy(r -> r.getUser().getId().toString()));
        response.setRecommendationsByUser(recommendationsByUser);

        double avgScore = recommendationDTOs.stream()
                .mapToDouble(RecommendationDTO::getPriorityScore)
                .average().orElse(0.0);
        response.setAveragePriorityScore(avgScore);

        long highPriorityCount = recommendationDTOs.stream()
                .filter(r -> r.getPriorityScore() > 6.0)
                .count();
        response.setHighPriorityCount(highPriorityCount);

        return response;
    }

    private List<Recommendation> generateRecommendationsInternal() {
        KieSession kieSession = null;
        try {
            kieSession = kieContainer.newKieSession("fwKsession");

            // Get existing data from database
            List<User> users = userRepository.findAll();
            List<Post> posts = postRepository.findAll();

            // Insert users and posts
            for (User user : users) {
                kieSession.insert(user);
            }
            for (Post post : posts) {
                kieSession.insert(post);
            }

            // ===== ADD TRENDING HASHTAGS to trigger Level 3 =====
            TrendingHashtag trend1 = new TrendingHashtag("AI");
            TrendingHashtag trend2 = new TrendingHashtag("Innovation");
            TrendingHashtag trend3 = new TrendingHashtag("TechNews");
            TrendingHashtag trend4 = new TrendingHashtag("Football");
            TrendingHashtag trend5 = new TrendingHashtag("Highlights");
            TrendingHashtag trend6 = new TrendingHashtag("SportsUpdate");

            kieSession.insert(trend1);
            kieSession.insert(trend2);
            kieSession.insert(trend3);
            kieSession.insert(trend4);
            kieSession.insert(trend5);
            kieSession.insert(trend6);

            // ===== ADD USER GOALS to trigger Level 5 and 5b =====
            for (User user : users) {
                if (user.getId() != null) {
                    // Create an engagement goal for each user
                    UserGoal engagementGoal = new UserGoal();
                    engagementGoal.setUserId(user.getId());
                    engagementGoal.setTarget("engagement");
                    kieSession.insert(engagementGoal);

                    // Optionally add a reach goal as well
                    UserGoal reachGoal = new UserGoal();
                    reachGoal.setUserId(user.getId());
                    reachGoal.setTarget("reach");
                    kieSession.insert(reachGoal);
                }
            }

            System.out.println("=== Drools Session Facts ===");
            System.out.println("Users inserted: " + users.size());
            System.out.println("Posts inserted: " + posts.size());
            System.out.println("Trending hashtags inserted: 6");
            System.out.println("User goals inserted: " + (users.size() * 2));
            System.out.println("============================");

            // Fire all rules
            int rulesFired = kieSession.fireAllRules();
            System.out.println("Total rules fired: " + rulesFired);

            // Collect recommendations
            List<Recommendation> recommendations = new ArrayList<>();
            for (Object fact : kieSession.getObjects(o -> o instanceof Recommendation)) {
                recommendations.add((Recommendation) fact);
            }

            recommendations.sort(Comparator.comparing(Recommendation::getPriorityScore).reversed());

            System.out.println("Total recommendations created: " + recommendations.size());
            System.out.println("============================");

            return recommendations;

        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }
    /**
     * Demonstrira ulančano CEP pravilo.
     * Prvo pravilo detektuje trend, a drugo proverava njegovu relevantnost za datog korisnika.
     */
    public List<RelevantTrendAlert> detectAndAnalyzeRelevantTrends(User user) {
        KieSession kieSession = kieContainer.newKieSession("cepKsession");
        try {
            SessionPseudoClock clock = kieSession.getSessionClock();
            List<RelevantTrendAlert> relevantTrendAlerts = new ArrayList<>();

            kieSession.insert(user);

            // Simulacija istorijske upotrebe heštega (baseline)
            for (int i = 0; i < 14; i++) {
                kieSession.insert(new HashtagUsageEvent("#fitness"));
                clock.advanceTime(12, TimeUnit.HOURS);
            }

            // Simulacija naglog skoka popularnosti
            log.info("Simulating hashtag spike for #fitness...");
            for (int i = 0; i < 40; i++) {
                kieSession.insert(new HashtagUsageEvent("#fitness"));
                clock.advanceTime(9, TimeUnit.MINUTES);
            }

            kieSession.fireAllRules();

            for (Object fact : kieSession.getObjects(o -> o instanceof RelevantTrendAlert)) {
                relevantTrendAlerts.add((RelevantTrendAlert) fact);
            }
            return relevantTrendAlerts;
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    /**
     * Demonstrira kompleksno CEP pravilo za detekciju zasićenja publike.
     * Simulira scenario gde korisnik prečesto objavljuje, što dovodi do pada engagementa i deljenja.
     */
    public Optional<AudienceSaturationAlert> detectAudienceSaturation() {
        KieSession kieSession = kieContainer.newKieSession("cepKsession");
        try {
            SessionPseudoClock clock = kieSession.getSessionClock();
            String saturatedCategory = "vežbe";

            // Priprema: Postavljanje istorijskog proseka za "share"
            for (int i = 0; i < 5; i++) {
                long postId = 100L + i;
                kieSession.insert(new PostPublishedEvent(postId, saturatedCategory));
                kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.SHARE));
                kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.SHARE));
                clock.advanceTime(5, TimeUnit.DAYS);
            }

            log.info("Simulating audience saturation scenario for category '{}'...", saturatedCategory);
            // Simulacija scenarija zasićenja
            for (int i = 0; i < 11; i++) {
                long postId = (long) i;
                kieSession.insert(new PostPublishedEvent(postId, saturatedCategory));

                if (i < 5) { // Stariji period
                    for (int j = 0; j < 10; j++) kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.LIKE));
                } else { // Noviji period
                    for (int j = 0; j < 3; j++) kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.LIKE));
                }

                if (i == 3 || i == 8) {
                    kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.SHARE));
                }

                clock.advanceTime(1, TimeUnit.DAYS);
            }

            kieSession.fireAllRules();

            return kieSession.getObjects(o -> o instanceof AudienceSaturationAlert)
                    .stream()
                    .map(o -> (AudienceSaturationAlert) o)
                    .findFirst();
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }
}