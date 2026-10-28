package com.twitter.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TweetInteractionId implements Serializable {
    @Column(name = "tweet_id", nullable = false)
    private Long tweetId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "type", nullable = false)
    private Byte type;

    public TweetInteractionId() {
    }

    public TweetInteractionId(Long tweetId, String userId, Byte type) {
        this.tweetId = tweetId;
        this.userId = userId;
        this.type = type;
    }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Byte getType() { return type; }
    public void setType(Byte type) { this.type = type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TweetInteractionId)) return false;
        TweetInteractionId that = (TweetInteractionId) o;
        return Objects.equals(tweetId, that.tweetId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweetId, userId, type);
    }
}
