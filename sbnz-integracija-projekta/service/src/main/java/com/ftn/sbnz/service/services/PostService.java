package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.dto.request.PostCreateDTO;
import com.ftn.sbnz.model.dto.response.PostResponseDTO;
import com.ftn.sbnz.model.events.PostPublishedEvent;
import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repositories.PostRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final KieSession cepKsession;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, @Qualifier("cepKsession") KieSession cepKsession) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cepKsession = cepKsession;
    }

    public PostResponseDTO createPost(PostCreateDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        Post newPost = new Post();
        newPost.setUser(user);
        newPost.setContent(dto.getContent());
        newPost.setContentType(dto.getContentType());
        newPost.setCategory(dto.getCategory());
        newPost.setHashtags(dto.getHashtags());
        newPost.setPublishTime(LocalDateTime.now());

        Post savedPost = postRepository.save(newPost);

        try {
            log.info("Ubacujem PostPublishedEvent u CEP sesiju: Post ID {}, Kategorija {}", savedPost.getId(), savedPost.getCategory());
            cepKsession.insert(new PostPublishedEvent(savedPost.getId(), savedPost.getCategory()));
            log.info("Događaj uspešno ubačen.");
        } catch (Exception e) {
            log.error("!!! GREŠKA PRILIKOM UBACIVANJA DOGAĐAJA U CEP SESIJU !!!", e);

        }

        return new PostResponseDTO(newPost);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return postRepository.findAllByUser(user);
    }
}