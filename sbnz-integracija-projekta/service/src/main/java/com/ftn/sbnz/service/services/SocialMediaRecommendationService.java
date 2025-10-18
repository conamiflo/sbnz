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
    private final KieSession cepKsession; // Singleton sesija za CEP

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
//    public Optional<AudienceSaturationAlert> detectAudienceSaturation() {
//        KieSession kieSession = kieContainer.newKieSession("cepKsession");
//        try {
//            SessionPseudoClock clock = kieSession.getSessionClock();
//            String saturatedCategory = "vezbe"; // uskladi sa DRL-om!
//
//            // ---- BASELINE: rasprši kroz ~70 dana, SHARE kao metrika ----
//            for (int i = 0; i < 14; i++) {
//                long postId = 100L + i;
//                Date t = new Date(clock.getCurrentTime());
//                kieSession.insert(new PostPublishedEvent(postId, saturatedCategory, t));
//                for (int j = 0; j < 6; j++) {
//                    kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.SHARE, t));
//                }
//                clock.advanceTime(5, TimeUnit.DAYS);
//            }
//
//            log.info("Simulating audience saturation (drop in SHARE) for category '{}'...", saturatedCategory);
//
//            // ---- RECENT: poslednjih 7 dana, drastično manje SHARE-ova ----
//            for (int i = 0; i < 7; i++) {
//                long postId = 200L + i;
//                Date t = new Date(clock.getCurrentTime());
//                kieSession.insert(new PostPublishedEvent(postId, saturatedCategory, t));
//                for (int j = 0; j < 1; j++) {
//                    kieSession.insert(new EngagementEvent(postId, saturatedCategory, EngagementEvent.EngagementType.SHARE, t));
//                }
//                clock.advanceTime(1, TimeUnit.DAYS);
//            }
//
//            // probudi pravila, ako koristiš timer/prozore
//            kieSession.insert(clock.getCurrentTime());
//            kieSession.fireAllRules();
//
//            return kieSession.getObjects(o -> o instanceof AudienceSaturationAlert)
//                    .stream()
//                    .map(o -> (AudienceSaturationAlert) o)
//                    .findFirst();
//
//        } finally {
//            if (kieSession != null) {
//                kieSession.dispose();
//            }
//        }
//    }

    @Transactional(readOnly = true) // Osigurava da sesija ostane otvorena za LAZY učitavanje
    public List<AudienceSaturationAlert> analyzeRealDataOnDemand() {
        log.info("Pokretanje analize stvarnih podataka na zahtev...");

        // 1. Definiši vremenski prozor za analizu (pravilo gleda do 60 dana unazad)
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);

        // 2. Dohvati sve relevantne postove i engagemente iz baze
        List<Post> recentPosts = postRepository.findByPublishTimeAfter(sixtyDaysAgo);
        List<Engagement> recentEngagements = engagementRepository.findByTimestampAfter(sixtyDaysAgo);

        log.info("Analiziram {} postova i {} engagement događaja iz baze.", recentPosts.size(), recentEngagements.size());

        // 3. Ubaci sve kao događaje u glavnu CEP sesiju
        // (Pretpostavka je da cepKsession radi u `fireUntilHalt` modu)
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

        // 4. Ubaci "sada" da bi se pravila koja porede sa $now aktivirala
        cepKsession.insert(System.currentTimeMillis());

        // 5. Vrati rezultat tako što pokupiš alerte iz sesije
        return getAudienceSaturationAlerts();
    }


    /**
     * "Get" metoda za frontend.
     * Proverava da li u glavnoj CEP sesiji postoje aktivni alerti,
     * vraća ih i briše iz sesije da se ne bi ponovo prikazivali.
     */
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

            // SAT POČINJE OD 0 (ili neke početne tačke)
            long startTime = clock.getCurrentTime();

            // KORAK 1: Ubaci baseline događaje (30-7 dana unazad)
            log.info("Simuliram baseline (od pre 30d do pre 7d)...");
            for (int i = 0; i < 5; i++) {
                long postId = 100L + i;
                // Postavi sat na odgovarajuće vreme
                long eventTime = startTime + TimeUnit.DAYS.toMillis(i * 5);
                clock.advanceTime(TimeUnit.DAYS.toMillis(i * 5) - (i > 0 ? TimeUnit.DAYS.toMillis((i-1) * 5) : 0), TimeUnit.MILLISECONDS);

                Date t = new Date(clock.getCurrentTime());
                testSession.insert(new PostPublishedEvent(postId, category, t));
                for (int j = 0; j < 5; j++) {
                    testSession.insert(new EngagementEvent(postId, category, EngagementEvent.EngagementType.SHARE, t));
                }
            }

            // Pomeri sat na trenutak pre 7 dana od kraja
            clock.advanceTime(TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);

            // KORAK 2: Ubaci recent događaje (zadnjih 7 dana)
            log.info("Simuliram A (Overposting u 48h) i B (Recent Share Drop u 7d)...");
            for (int i = 0; i < 8; i++) {
                long postId = 200L + i;
                clock.advanceTime(TimeUnit.HOURS.toMillis(6), TimeUnit.MILLISECONDS);

                Date t = new Date(clock.getCurrentTime());
                testSession.insert(new PostPublishedEvent(postId, category, t));
                testSession.insert(new EngagementEvent(postId, category, EngagementEvent.EngagementType.SHARE, t));
            }

            // KORAK 3: Trenutno vreme je SADA automatski
            long now = clock.getCurrentTime();
            testSession.insert(now);

            // KORAK 4: Pokreni pravila
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

            // SAT POČINJE OD 0
            long startTime = clock.getCurrentTime();

            // KORAK 1: Simuliraj BASELINE aktivnost (rasporedi kroz 24h)
            log.info("Step 1: Simulating baseline follower activity (24h)...");
            for (int i = 0; i < 24; i++) {
                // Pomeri sat na sledeći sat
                if (i > 0) {
                    clock.advanceTime(1, TimeUnit.HOURS);
                }

                Date t = new Date(clock.getCurrentTime());
                kieSession.insert(new EngagementEvent(1L + i, "fitness", EngagementEvent.EngagementType.LIKE, t));
                kieSession.insert(new EngagementEvent(100L + i, "fitness", EngagementEvent.EngagementType.COMMENT, t));
            }
            log.info("Baseline: 48 engagements over 24h (current time: {})", new Date(clock.getCurrentTime()));

            // KORAK 2: Simuliraj SPIKE u aktivnosti (koncentrisano u 2h)
            log.info("Step 2: Simulating engagement SPIKE (2h window)...");
            for (int i = 0; i < 25; i++) {
                clock.advanceTime(4, TimeUnit.MINUTES); // Pomeri sat napred

                Date t = new Date(clock.getCurrentTime());
                EngagementEvent.EngagementType type = (i % 2 == 0)
                        ? EngagementEvent.EngagementType.LIKE
                        : EngagementEvent.EngagementType.COMMENT;
                kieSession.insert(new EngagementEvent(200L + i, "fitness", type, t));
            }
            log.info("Spike: 25 engagements in last 2h (current time: {})", new Date(clock.getCurrentTime()));

            // KORAK 3: Simuliraj trending hashtag
            log.info("Step 3: Simulating trending hashtag relevant to user interests...");
            String relevantHashtag = "#" + (user.getInterests().isEmpty() ? "fitness" : user.getInterests().get(0));

            // Vrati sat unazad za baseline hashtag-a
            long currentTime = clock.getCurrentTime();
            long sevenDaysAgo = currentTime - TimeUnit.DAYS.toMillis(7);

            // Simuliraj baseline za hashtag (raspodeljeno kroz 7 dana)
            for (int i = 0; i < 14; i++) {
                long eventTime = sevenDaysAgo + (i * TimeUnit.HOURS.toMillis(12));
                HashtagUsageEvent hashtagEvent = new HashtagUsageEvent(relevantHashtag);
                hashtagEvent.setTimestamp(new Date(eventTime));
                kieSession.insert(hashtagEvent);
            }

            // Spike za hashtag (poslednje 6h)
            log.info("Creating hashtag spike for: {}", relevantHashtag);
            long sixHoursAgo = currentTime - TimeUnit.HOURS.toMillis(6);
            for (int i = 0; i < 40; i++) {
                long eventTime = sixHoursAgo + (i * TimeUnit.MINUTES.toMillis(9));
                HashtagUsageEvent hashtagEvent = new HashtagUsageEvent(relevantHashtag);
                hashtagEvent.setTimestamp(new Date(eventTime));
                kieSession.insert(hashtagEvent);
            }

            // KORAK 4: Fire all rules
            log.info("Step 4: Firing all CEP rules...");
            int rulesFired = kieSession.fireAllRules();
            log.info("Total rules fired: {}", rulesFired);

            // KORAK 5: Collect alerts
            for (Object fact : kieSession.getObjects(o -> o instanceof ViralMomentumAlert)) {
                ViralMomentumAlert alert = (ViralMomentumAlert) fact;
                alerts.add(alert);
                log.info("✅ VIRAL MOMENTUM ALERT: {}", alert.getMessage());
            }

            if (alerts.isEmpty()) {
                log.warn("⚠️ No viral momentum detected. Checking intermediate facts...");

                // Debug - proveri intermedijarne činjenice
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

    /**
     * Getter metoda za frontend - vraća aktivne alerte iz singleton sesije
     */
    public List<ViralMomentumAlert> getViralMomentumAlerts() {
        List<ViralMomentumAlert> alerts = new ArrayList<>();

        for (Object fact : cepKsession.getObjects(o -> o instanceof ViralMomentumAlert)) {
            ViralMomentumAlert alert = (ViralMomentumAlert) fact;
            alerts.add(alert);
            // Obriši nakon čitanja da se ne vraćaju stalno isti alerti
            cepKsession.delete(cepKsession.getFactHandle(alert));
        }

        return alerts;
    }

}