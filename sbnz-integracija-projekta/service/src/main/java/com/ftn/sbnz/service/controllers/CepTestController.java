package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.AudienceSaturationAlert;
import com.ftn.sbnz.model.models.RelevantTrendAlert;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.model.models.ViralMomentumAlert;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.SocialMediaRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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

    @GetMapping("/relevant-trend/{username}")
    public ResponseEntity<List<RelevantTrendAlert>> testRelevantTrend(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        List<RelevantTrendAlert> alerts = recommendationService.detectAndAnalyzeRelevantTrends(user);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/audience-saturation")
    public ResponseEntity<?> testAudienceSaturation() {
        Optional<AudienceSaturationAlert> alert = recommendationService.detectAudienceSaturation();
        return alert.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok("No audience saturation detected."));
    }

    @GetMapping("/viral-momentum/{username}")
    public ResponseEntity<?> testViralMomentum(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        List<ViralMomentumAlert> alerts = recommendationService.detectViralMomentumWindow(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "alertsDetected", alerts.size(),
                "alerts", alerts
        ));
    }
}