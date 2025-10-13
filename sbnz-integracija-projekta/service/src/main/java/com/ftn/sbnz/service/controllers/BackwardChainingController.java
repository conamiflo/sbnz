package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.Recommendation;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repositories.PostRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import com.ftn.sbnz.service.services.BackwardChainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/backward")
public class BackwardChainingController {

    @Autowired
    private BackwardChainingService backwardChainingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts = postRepository.findAll();

        List<Recommendation> recommendations =
                backwardChainingService.findRelevantContent(user, posts);

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/connected/{userId}")
    public ResponseEntity<List<Recommendation>> getConnectedContent(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts = postRepository.findAll();

        List<Recommendation> recommendations =
                backwardChainingService.findConnectedContent(user, posts);

        return ResponseEntity.ok(recommendations);
    }
}