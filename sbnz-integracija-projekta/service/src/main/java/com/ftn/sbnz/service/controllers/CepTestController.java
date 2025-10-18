package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.AudienceSaturationAlert;
import com.ftn.sbnz.model.models.RelevantTrendAlert;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.model.models.ViralMomentumAlert;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.SocialMediaRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cep-tests")
public class CepTestController {

    private final SocialMediaRecommendationService recommendationService;
    private final UserRepository userRepository;

    public CepTestController(SocialMediaRecommendationService recommendationService, UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/relevant-trend/{userId}")
    public ResponseEntity<List<RelevantTrendAlert>> testRelevantTrend(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<RelevantTrendAlert> alerts = recommendationService.detectAndAnalyzeRelevantTrends(user);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/audience-saturation")
    public ResponseEntity<?> testAudienceSaturation() {
        Optional<AudienceSaturationAlert> alert = recommendationService.detectAudienceSaturation();
        return alert.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok("No audience saturation detected."));
    }

    @GetMapping("/viral-momentum/{userId}")
    public ResponseEntity<?> testViralMomentum(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ViralMomentumAlert> alerts = recommendationService.detectViralMomentumWindow(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "alertsDetected", alerts.size(),
                "alerts", alerts
        ));
    }
}