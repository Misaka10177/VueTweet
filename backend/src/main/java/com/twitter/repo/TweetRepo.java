package com.twitter.repo;

import com.twitter.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepo extends JpaRepository<Tweet, Long> {
    List<Tweet> findByReplyToOrderByCreatedAtDesc(Tweet replyTo);
}
