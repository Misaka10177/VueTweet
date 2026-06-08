package com.twitter.repo;

import com.twitter.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetRepo extends JpaRepository<Tweet, Long> {
}
