package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.dto.request.PostCreateDTO;
import com.ftn.sbnz.model.dto.response.PostResponseDTO;
import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.service.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
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