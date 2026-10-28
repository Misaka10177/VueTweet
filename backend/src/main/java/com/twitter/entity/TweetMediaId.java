package com.twitter.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TweetMediaId implements Serializable {
    @Column(name = "tweet_id", nullable = false)
    private Long tweetId;

    @Column(name = "sort_order", nullable = false)
    private Byte sortOrder;

    public TweetMediaId() {
    }

    public TweetMediaId(Long tweetId, Byte sortOrder) {
        this.tweetId = tweetId;
        this.sortOrder = sortOrder;
    }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public Byte getSortOrder() { return sortOrder; }
    public void setSortOrder(Byte sortOrder) { this.sortOrder = sortOrder; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TweetMediaId)) return false;
        TweetMediaId that = (TweetMediaId) o;
        return Objects.equals(tweetId, that.tweetId)
                && Objects.equals(sortOrder, that.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweetId, sortOrder);
    }
}
