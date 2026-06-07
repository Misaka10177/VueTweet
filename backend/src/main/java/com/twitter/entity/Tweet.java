package com.twitter.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tweet")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private Tweet replyTo;

    private int reply;
    private int transpond;
    private int upvote;

    @Column(name = "view_count")
    private int viewCount;

    private int bookmark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Tweet getReplyTo() { return replyTo; }
    public void setReplyTo(Tweet replyTo) { this.replyTo = replyTo; }
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
