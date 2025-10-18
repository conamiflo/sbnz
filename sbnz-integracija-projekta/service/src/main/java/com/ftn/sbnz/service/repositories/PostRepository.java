package com.ftn.sbnz.service.repositories;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
    List<Post> findByPublishTimeAfter(LocalDateTime timestamp);

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.user ORDER BY p.publishTime DESC")
    List<Post> findAllWithUser();

}