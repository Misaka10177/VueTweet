package com.twitter.repo;

import com.twitter.entity.TweetReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetReplyRepo extends JpaRepository<TweetReply, Long> {
    List<TweetReply> findByTweetId(Long tweetId);
    TweetReply findByTweetIdAndReplyId(Long tweetId, Long replyId);
    TweetReply findByReplyId(Long replyId);
}
