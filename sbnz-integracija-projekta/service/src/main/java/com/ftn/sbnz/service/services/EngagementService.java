package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.events.EngagementEvent;
import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.service.repositories.PostRepository;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Service
public class EngagementService {

    private final PostRepository postRepository;
    private final KieSession cepKsession;
    private static final Logger log = LoggerFactory.getLogger(EngagementService.class);

    @Autowired
    public EngagementService(PostRepository postRepository, @Qualifier("cepKsession") KieSession cepKsession) {
        this.postRepository = postRepository;
        this.cepKsession = cepKsession;
    }

    // Primer metode koju tvoj kontroler poziva kad neko lajkuje post
    public void addEngagement(Long postId, EngagementEvent.EngagementType type) {
        // (Ovde bi išla tvoja logika za čuvanje lajka/komentara u bazu)

        // Dohvati post da bismo znali kategoriju
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        try {
            log.info("Ubacujem EngagementEvent u CEP sesiju: Tip {}", type);

            // ✅ Ubaci događaj u podrazumevani entry point
            cepKsession.insert(new EngagementEvent(postId, post.getCategory(), type, new Date()));

            log.info("Događaj uspešno ubačen.");
        } catch (Exception e) {
            log.error("!!! GREŠKA PRILIKOM UBACIVANJA DOGAĐAJA U CEP SESIJU !!!", e);
        }
    }
}