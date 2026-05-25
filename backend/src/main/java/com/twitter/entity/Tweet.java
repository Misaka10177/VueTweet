package com.twitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "tweet")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String text;

    private int reply;
    private int transpond;
    private int upvote;

    @Column(name = "view_count")
    private int viewCount;

    @Column(columnDefinition = "TEXT")
    private String images;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getReply() { return reply; }
    public void setReply(int reply) { this.reply = reply; }
    public int getTranspond() { return transpond; }
    public void setTranspond(int transpond) { this.transpond = transpond; }
    public int getUpvote() { return upvote; }
    public void setUpvote(int upvote) { this.upvote = upvote; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
}
