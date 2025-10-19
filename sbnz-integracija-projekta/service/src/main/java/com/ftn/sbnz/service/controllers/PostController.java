package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.dto.request.PostCreateDTO;
import com.ftn.sbnz.model.dto.response.PostResponseDTO;
import com.ftn.sbnz.service.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostCreateDTO postCreateDTO) {
        PostResponseDTO createdPost = postService.createPost(postCreateDTO);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long postId) {
        try {
            PostResponseDTO post = postService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/user/id/{userId}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByUserId(@PathVariable Long userId) {
        List<PostResponseDTO> postDTOs = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(postDTOs);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByUsername(@PathVariable String username) {
        try {
            List<PostResponseDTO> postDTOs = postService.getPostsByUsername(username);
            return ResponseEntity.ok(postDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponseDTO> likePost(@PathVariable Long postId) {
        try {
            PostResponseDTO updatedPost = postService.incrementLikes(postId);
            return ResponseEntity.ok(updatedPost);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<PostResponseDTO> sharePost(@PathVariable Long postId) {
        try {
            PostResponseDTO updatedPost = postService.incrementShares(postId);
            return ResponseEntity.ok(updatedPost);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<PostResponseDTO> commentPost(@PathVariable Long postId) {
        try {
            PostResponseDTO updatedPost = postService.incrementComments(postId);
            return ResponseEntity.ok(updatedPost);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}