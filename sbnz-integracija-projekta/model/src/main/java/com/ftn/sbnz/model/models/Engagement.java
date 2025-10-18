package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.events.EngagementEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "engagements")
public class Engagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngagementEvent.EngagementType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Engagement(Post post, EngagementEvent.EngagementType type, LocalDateTime timestamp) {
        this.post = post;
        this.type = type;
        this.timestamp = timestamp;
    }
}