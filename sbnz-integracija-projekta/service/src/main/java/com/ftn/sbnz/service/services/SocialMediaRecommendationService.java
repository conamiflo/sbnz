package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.dto.response.RecommendationResponse;
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
        List<Recommendation> recommendations = this.generateRecommendationsInternal();

        RecommendationResponse response = new RecommendationResponse();
        response.setSuccess(true);
        response.setRecommendations(recommendations);
        response.setTotalCount(recommendations.size());
        response.setMessage("Successfully generated " + recommendations.size() + " recommendations");

        // KLJUČNA IZMENA: Ovaj kod je sada usklađen sa novom Recommendation klasom
        // jer koristi r.getUser().getId() umesto nepostojećeg r.getUserId()
        Map<String, List<Recommendation>> recommendationsByUser = recommendations.stream()
                .collect(Collectors.groupingBy(r -> {
                    if (r.getUser() != null && r.getUser().getId() != null) {
                        return r.getUser().getId().toString();
                    }
                    return "unknown";
                }));
        response.setRecommendationsByUser(recommendationsByUser);

        double avgScore = recommendations.stream()
                .mapToDouble(Recommendation::getPriorityScore)
                .average().orElse(0.0);
        response.setAveragePriorityScore(avgScore);

        long highPriorityCount = recommendations.stream()
                .filter(r -> r.getPriorityScore() > 6.0)
                .count();
        response.setHighPriorityCount(highPriorityCount);

        return response;
    }

    private List<Recommendation> generateRecommendationsInternal() {
        KieSession kieSession = null;
        try {
            kieSession = kieContainer.newKieSession("fwKsession");

            List<User> users = userRepository.findAll();
            List<Post> posts = postRepository.findAll();

            for (User user : users) {
                kieSession.insert(user);
            }
            for (Post post : posts) {
                kieSession.insert(post);
            }

            kieSession.fireAllRules();

            List<Recommendation> recommendations = new ArrayList<>();
            for (Object fact : kieSession.getObjects(o -> o instanceof Recommendation)) {
                recommendations.add((Recommendation) fact);
            }

            recommendations.sort(Comparator.comparing(Recommendation::getPriorityScore).reversed());
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