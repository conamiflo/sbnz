package com.ftn.sbnz.service;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftn.sbnz.model.models.Recommendation;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private final KieContainer kieContainer;

    @Autowired
    public TemplateService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public List<Recommendation> fireRecommendations(List<Recommendation> recommendations) {
        KieSession kieSession = null;
        try {
            // Try to get a session specifically for template rules
            if (kieContainer != null) {
                try {
                    kieSession = kieContainer.newKieSession("templateKsession");
                    log.info("Using templateKsession for template rules");
                } catch (Exception e) {
                    log.warn("templateKsession not found, using default session: " + e.getMessage());
                    kieSession = kieContainer.newKieSession();
                }
            } else {
                log.warn("No KieContainer injected, using classpath container");
                KieServices kieServices = KieServices.Factory.get();
                KieContainer fallbackContainer = kieServices.getKieClasspathContainer();
                kieSession = fallbackContainer.newKieSession();
            }

            System.out.println("=== FIRING TEMPLATE RULES ===");
            System.out.println("Recommendations to process: " + recommendations.size());
            System.out.println();

            // Insert recommendations as facts
            for (Recommendation r : recommendations) {
                kieSession.insert(r);
                System.out.println("Inserted recommendation for user: " + r.getUserId()
                        + " | Post: " + r.getPostId());
            }

            System.out.println();
            int rulesTriggered = kieSession.fireAllRules();
            System.out.println("Total template rules triggered: " + rulesTriggered);
            System.out.println();

            // Collect any Recommendation objects created/modified by rules
            List<Recommendation> processed = new ArrayList<>();
            for (Object fact : kieSession.getObjects()) {
                if (fact instanceof Recommendation) {
                    processed.add((Recommendation) fact);
                }
            }

            System.out.println("Template processing complete. Total processed: " + processed.size());
            return processed;

        } catch (Exception e) {
            log.error("Error during template rule execution: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process recommendations with template rules: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }
}
