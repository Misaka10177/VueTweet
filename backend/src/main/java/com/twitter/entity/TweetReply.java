package com.twitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "tweet_reply", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tweet_id", "reply_id"})
})
public class TweetReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tweet_id")
    private Long tweetId;

    @Column(name = "reply_id")
    private Long replyId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public Long getReplyId() { return replyId; }
    public void setReplyId(Long replyId) { this.replyId = replyId; }
}
