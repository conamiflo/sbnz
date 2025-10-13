package com.ftn.sbnz.service.repositories;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
}