package com.twitter.repo;

import com.twitter.entity.TweetInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TweetInteractionRepo extends JpaRepository<TweetInteraction, Long> {
    Optional<TweetInteraction> findByTweetIdAndUserIdAndType(Long tweetId, String userId, String type);

    List<TweetInteraction> findByUserIdAndTweetIdIn(String userId, Collection<Long> tweetIds);

    long countByTweetIdAndType(Long tweetId, String type);
}
