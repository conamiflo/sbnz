package com.ftn.sbnz.service.repositories;

import com.ftn.sbnz.model.models.Engagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EngagementRepository extends JpaRepository<Engagement, Long> {

    List<Engagement> findByTimestampAfter(LocalDateTime timestamp);
}