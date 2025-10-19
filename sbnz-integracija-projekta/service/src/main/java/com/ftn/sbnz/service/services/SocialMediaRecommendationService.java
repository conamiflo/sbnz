package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.dto.response.RecommendationDTO;
import com.ftn.sbnz.model.dto.response.RecommendationResponse;
import com.ftn.sbnz.model.dto.response.UserDTO;
import com.ftn.sbnz.model.events.EngagementEvent;
import com.ftn.sbnz.model.events.HashtagUsageEvent;
import com.ftn.sbnz.model.events.PostPublishedEvent;
import com.ftn.sbnz.model.models.*;
import com.ftn.sbnz.service.repositories.EngagementRepository;
import com.ftn.sbnz.service.repositories.PostRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SocialMediaRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(SocialMediaRecommendationService.class);

    private final KieContainer kieContainer;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final EngagementRepository engagementRepository;
    private final KieSession cepKsession;

    @Autowired
    public SocialMediaRecommendationService(
            KieContainer kieContainer,
            UserRepository userRepository,
            PostRepository postRepository,
            EngagementRepository engagementRepository,
            @Qualifier("cepKsession") KieSession cepKsession
    ) {
        this.kieContainer = kieContainer;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.engagementRepository = engagementRepository;
        this.cepKsession = cepKsession;
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

            log.info("Inserting TrendingHashtag facts with popularity...");
            kieSession.insert(new TrendingHashtag("AI", 85));
            kieSession.insert(new TrendingHashtag("Java", 75));
            kieSession.insert(new TrendingHashtag("Spring", 72));
            kieSession.insert(new TrendingHashtag("fitness", 90));
            // Примери са мањом популарношћу (неће активирати правило Nivo 3)
            kieSession.insert(new TrendingHashtag("cooking", 50));
            kieSession.insert(new TrendingHashtag("travel", 65));


            log.info("Inserting UserGoal facts...");
            for (User user : users) {
                if (user.getId() != null) {
                    kieSession.insert(new UserGoal(user.getId(), "engagement"));
                    kieSession.insert(new UserGoal(user.getId(), "reach"));
                }
            }

            log.info("=== Drools Session Facts Before Firing ===");
            log.info("Users inserted: {}", users.size());
            log.info("Posts inserted: {}", posts.size());
            log.info("Trending hashtags inserted: 6");
            log.info("User goals inserted: {}", users.size() * 2);
            log.info("==========================================");

            int rulesFired = kieSession.fireAllRules();
            log.info("Total rules fired in fwKsession: {}", rulesFired);

            List<Recommendation> recommendations = new ArrayList<>();
            for (Object fact : kieSession.getObjects(o -> o instanceof Recommendation)) {
                recommendations.add((Recommendation) fact);
            }

            recommendations.sort(Comparator.comparing(Recommendation::getPriorityScore).reversed());

            log.info("Total recommendations collected after rules: {}", recommendations.size()); // Користи логер
            log.info("==========================================");

            return recommendations;

        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    public List<RelevantTrendAlert> detectAndAnalyzeRelevantTrends(User user) {
        KieSession kieSession = kieContainer.newKieSession("cepKsession");
        try {
            SessionPseudoClock clock = kieSession.getSessionClock();
            List<RelevantTrendAlert> relevantTrendAlerts = new ArrayList<>();

            kieSession.insert(user);

            for (int i = 0; i < 14; i++) {
                kieSession.insert(new HashtagUsageEvent("#fitness"));
                clock.advanceTime(12, TimeUnit.HOURS);
            }

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

    @Transactional(readOnly = true)
    public List<AudienceSaturationAlert> analyzeRealDataOnDemand() {
        log.info("Pokretanje analize stvarnih podataka na zahtev...");

        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);

        List<Post> recentPosts = postRepository.findByPublishTimeAfter(sixtyDaysAgo);
        List<Engagement> recentEngagements = engagementRepository.findByTimestampAfter(sixtyDaysAgo);

        log.info("Analiziram {} postova i {} engagement događaja iz baze.", recentPosts.size(), recentEngagements.size());

        for (Post post : recentPosts) {
            Date d = Date.from(post.getPublishTime().atZone(ZoneId.systemDefault()).toInstant());
            cepKsession.insert(new PostPublishedEvent(post.getId(), post.getCategory(), d));
        }
        for (Engagement engagement : recentEngagements) {
            Date d = Date.from(engagement.getTimestamp().atZone(ZoneId.systemDefault()).toInstant());
            if (engagement.getPost() != null) {
                cepKsession.insert(new EngagementEvent(engagement.getPost().getId(), engagement.getPost().getCategory(), engagement.getType(), d));
            }
        }

        cepKsession.insert(System.currentTimeMillis());

        return getAudienceSaturationAlerts();
    }


    public List<AudienceSaturationAlert> getAudienceSaturationAlerts() {
        List<AudienceSaturationAlert> alerts = new ArrayList<>();

        for (Object fact : cepKsession.getObjects(o -> o instanceof AudienceSaturationAlert)) {
            AudienceSaturationAlert alert = (AudienceSaturationAlert) fact;
            alerts.add(alert);

            cepKsession.delete(cepKsession.getFactHandle(alert));
        }

        if (!alerts.isEmpty()) {
            log.info("Pronađeno {} alerta o zasićenju publike.", alerts.size());
        }

        return alerts;
    }
    public Optional<AudienceSaturationAlert> detectAudienceSaturation() {
        KieSession testSession = kieContainer.newKieSession("cepKsession");
        try {
            SessionPseudoClock clock = testSession.getSessionClock();
            String category = "vezbe";
            log.info("--- Pokretanje NOVE simulacije za CEP pravila A, B, C, D ---");

            long startTime = clock.getCurrentTime();

            log.info("Simuliram baseline (od pre 30d do pre 7d)...");
            for (int i = 0; i < 5; i++) {
                long postId = 100L + i;
                long eventTime = startTime + TimeUnit.DAYS.toMillis(i * 5);
                clock.advanceTime(TimeUnit.DAYS.toMillis(i * 5) - (i > 0 ? TimeUnit.DAYS.toMillis((i-1) * 5) : 0), TimeUnit.MILLISECONDS);

                Date t = new Date(clock.getCurrentTime());
                testSession.insert(new PostPublishedEvent(postId, category, t));
                for (int j = 0; j < 5; j++) {
                    testSession.insert(new EngagementEvent(postId, category, EngagementEvent.EngagementType.SHARE, t));
                }
            }

            clock.advanceTime(TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);

            log.info("Simuliram A (Overposting u 48h) i B (Recent Share Drop u 7d)...");
            for (int i = 0; i < 8; i++) {
                long postId = 200L + i;
                clock.advanceTime(TimeUnit.HOURS.toMillis(6), TimeUnit.MILLISECONDS);

                Date t = new Date(clock.getCurrentTime());
                testSession.insert(new PostPublishedEvent(postId, category, t));
                testSession.insert(new EngagementEvent(postId, category, EngagementEvent.EngagementType.SHARE, t));
            }

            long now = clock.getCurrentTime();
            testSession.insert(now);

            log.info("Pokrecem pravila...");
            int rulesFired = testSession.fireAllRules();
            log.info("Broj aktiviranih pravila: {}", rulesFired);


            log.info("Simulacija završena. Tražim AudienceSaturationAlert...");
            Optional<AudienceSaturationAlert> result = testSession.getObjects(o -> o instanceof AudienceSaturationAlert)
                    .stream()
                    .map(o -> (AudienceSaturationAlert) o)
                    .findFirst();

            if (result.isPresent()) {
                log.info("!!! PRONAĐEN ALERT: {} !!!", result.get());
            } else {
                log.warn("--- ALERT NIJE PRONAĐEN. Proveriti logove pravila (ako postoje). ---");
            }

            return result;

        } finally {
            testSession.dispose();
        }
    }

    public List<ViralMomentumAlert> detectViralMomentumWindow(User user) {
        KieSession kieSession = kieContainer.newKieSession("cepKsession");
        try {
            SessionPseudoClock clock = kieSession.getSessionClock();
            List<ViralMomentumAlert> alerts = new ArrayList<>();

            kieSession.insert(user);
            log.info("=== Starting Viral Momentum Window detection for user: {} ===", user.getName());

            long startTime = clock.getCurrentTime();

            log.info("Step 1: Simulating baseline follower activity (24h)...");
            for (int i = 0; i < 24; i++) {
                if (i > 0) {
                    clock.advanceTime(1, TimeUnit.HOURS);
                }

                Date t = new Date(clock.getCurrentTime());
                kieSession.insert(new EngagementEvent(1L + i, "fitness", EngagementEvent.EngagementType.LIKE, t));
                kieSession.insert(new EngagementEvent(100L + i, "fitness", EngagementEvent.EngagementType.COMMENT, t));
            }
            log.info("Baseline: 48 engagements over 24h (current time: {})", new Date(clock.getCurrentTime()));

            log.info("Step 2: Simulating engagement SPIKE (2h window)...");
            for (int i = 0; i < 25; i++) {
                clock.advanceTime(4, TimeUnit.MINUTES);

                Date t = new Date(clock.getCurrentTime());
                EngagementEvent.EngagementType type = (i % 2 == 0)
                        ? EngagementEvent.EngagementType.LIKE
                        : EngagementEvent.EngagementType.COMMENT;
                kieSession.insert(new EngagementEvent(200L + i, "fitness", type, t));
            }
            log.info("Spike: 25 engagements in last 2h (current time: {})", new Date(clock.getCurrentTime()));

            log.info("Step 3: Simulating trending hashtag relevant to user interests...");
            String relevantHashtag = "#" + (user.getInterests().isEmpty() ? "fitness" : user.getInterests().get(0));

            long currentTime = clock.getCurrentTime();
            long sevenDaysAgo = currentTime - TimeUnit.DAYS.toMillis(7);

            for (int i = 0; i < 14; i++) {
                long eventTime = sevenDaysAgo + (i * TimeUnit.HOURS.toMillis(12));
                HashtagUsageEvent hashtagEvent = new HashtagUsageEvent(relevantHashtag);
                hashtagEvent.setTimestamp(new Date(eventTime));
                kieSession.insert(hashtagEvent);
            }

            log.info("Creating hashtag spike for: {}", relevantHashtag);
            long sixHoursAgo = currentTime - TimeUnit.HOURS.toMillis(6);
            for (int i = 0; i < 40; i++) {
                long eventTime = sixHoursAgo + (i * TimeUnit.MINUTES.toMillis(9));
                HashtagUsageEvent hashtagEvent = new HashtagUsageEvent(relevantHashtag);
                hashtagEvent.setTimestamp(new Date(eventTime));
                kieSession.insert(hashtagEvent);
            }

            log.info("Step 4: Firing all CEP rules...");
            int rulesFired = kieSession.fireAllRules();
            log.info("Total rules fired: {}", rulesFired);

            for (Object fact : kieSession.getObjects(o -> o instanceof ViralMomentumAlert)) {
                ViralMomentumAlert alert = (ViralMomentumAlert) fact;
                alerts.add(alert);
                log.info("VIRAL MOMENTUM ALERT: {}", alert.getMessage());
            }

            if (alerts.isEmpty()) {
                log.warn("No viral momentum detected. Checking intermediate facts...");

                long surgeFacts = kieSession.getObjects(o -> o instanceof FollowerEngagementSurge).size();
                long trendFacts = kieSession.getObjects(o -> o instanceof NicheTrendActive).size();
                log.warn("DEBUG: FollowerEngagementSurge facts: {}, NicheTrendActive facts: {}", surgeFacts, trendFacts);
            }

            return alerts;

        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    public List<ViralMomentumAlert> getViralMomentumAlerts() {
        List<ViralMomentumAlert> alerts = new ArrayList<>();

        for (Object fact : cepKsession.getObjects(o -> o instanceof ViralMomentumAlert)) {
            ViralMomentumAlert alert = (ViralMomentumAlert) fact;
            alerts.add(alert);
            cepKsession.delete(cepKsession.getFactHandle(alert));
        }

        return alerts;
    }

    public List<RelevantTrendAlert> getRelevantTrendAlerts() {
        List<RelevantTrendAlert> alerts = new ArrayList<>();
        List<FactHandle> handlesToDelete = new ArrayList<>();

        // Пронађи све RelevantTrendAlert објекте користећи FactHandle
        for (FactHandle handle : cepKsession.getFactHandles(o -> o instanceof RelevantTrendAlert)) {
            Object fact = cepKsession.getObject(handle);
            if (fact instanceof RelevantTrendAlert) {
                RelevantTrendAlert alert = (RelevantTrendAlert) fact;
                alerts.add(alert);
                handlesToDelete.add(handle);
            }
        }

        for (FactHandle handle : handlesToDelete) {
            cepKsession.delete(handle);
        }

        if (!alerts.isEmpty()) {
            log.info("Pronađeno {} Relevant Trend alerta.", alerts.size());
        }
        return alerts;
    }
}