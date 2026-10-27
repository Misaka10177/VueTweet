package com.twitter.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tweet")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "reply_to_id")
    private Long replyToId;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int reply;

    @Column(nullable = false)
    private int transpond;

    @Column(nullable = false)
    private int upvote;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int bookmark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public int getReply() { return reply; }
    public void setReply(int reply) { this.reply = reply; }
    public int getTranspond() { return transpond; }
    public void setTranspond(int transpond) { this.transpond = transpond; }
    public int getUpvote() { return upvote; }
    public void setUpvote(int upvote) { this.upvote = upvote; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getBookmark() { return bookmark; }
    public void setBookmark(int bookmark) { this.bookmark = bookmark; }
}
