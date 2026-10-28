package com.twitter.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tweet_interaction")
public class TweetInteraction {
    @EmbeddedId
    private TweetInteractionId id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TweetInteractionId getId() { return id; }
    public void setId(TweetInteractionId id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
