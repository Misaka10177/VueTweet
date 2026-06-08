package com.twitter.repo;

import com.twitter.entity.TweetInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetInteractionRepo extends JpaRepository<TweetInteraction, Long> {
    TweetInteraction findByTweetIdAndUserIdAndType(Long tweetId, String userId, String type);
}
