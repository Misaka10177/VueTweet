package com.twitter.repo;

import com.twitter.entity.TweetMedia;
import com.twitter.entity.TweetMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TweetMediaRepo extends JpaRepository<TweetMedia, TweetMediaId> {
    @Query("select m from TweetMedia m where m.id.tweetId in :tweetIds " +
            "order by m.id.tweetId asc, m.id.sortOrder asc")
    List<TweetMedia> findForTweets(@Param("tweetIds") Collection<Long> tweetIds);

    @Query("select m from TweetMedia m where m.id.tweetId = :tweetId order by m.id.sortOrder asc")
    List<TweetMedia> findForTweet(@Param("tweetId") Long tweetId);
}
