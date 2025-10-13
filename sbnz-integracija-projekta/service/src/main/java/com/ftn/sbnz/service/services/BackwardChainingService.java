package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.Recommendation;
import com.ftn.sbnz.model.models.User;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class BackwardChainingService {

    private static final Logger log = LoggerFactory.getLogger(BackwardChainingService.class);

    public List<Recommendation> findRelevantContent(User user, List<Post> posts) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession kieSession = null;

        try {
            // Use the backward chaining session from kmodule.xml
            kieSession = kc.newKieSession("bwKsession");

            // Insert user
            kieSession.insert(user);
            log.info("Inserted user: {}", user.getName());

            // Insert all posts
            for (Post post : posts) {
                kieSession.insert(post);
            }
            log.info("Inserted {} posts", posts.size());

            // Fire all rules - backward queries execute automatically
            int rulesFired = kieSession.fireAllRules();
            log.info("✓ Fired {} rules for backward chaining", rulesFired);

            // Collect all Recommendation objects
            List<Recommendation> recommendations = new ArrayList<>();
            for (Object obj : kieSession.getObjects()) {
                if (obj instanceof Recommendation) {
                    recommendations.add((Recommendation) obj);
                }
            }

            log.info("✓ Found {} recommendations", recommendations.size());
            return recommendations;

        } catch (Exception e) {
            log.error("Error in backward chaining: ", e);
            throw new RuntimeException("Backward chaining failed", e);
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    public List<Recommendation> findConnectedContent(User user, List<Post> posts) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSession kieSession = null;

        try {
            kieSession = kc.newKieSession("bwKsession");

            // Insert user and posts
            kieSession.insert(user);
            for (Post post : posts) {
                kieSession.insert(post);
            }

            // Insert trigger for "Find Connected Content" rule
            kieSession.insert("findConnected");
            log.info("✓ Trigger inserted for connected content search");

            int rulesFired = kieSession.fireAllRules();
            log.info("✓ Fired {} rules for connected content", rulesFired);

            // Collect recommendations
            List<Recommendation> recommendations = new ArrayList<>();
            for (Object obj : kieSession.getObjects()) {
                if (obj instanceof Recommendation) {
                    recommendations.add((Recommendation) obj);
                }
            }

            return recommendations;

        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }
}