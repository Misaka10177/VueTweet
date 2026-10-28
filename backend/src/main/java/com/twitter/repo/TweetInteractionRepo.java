package com.twitter.repo;

import com.twitter.entity.TweetInteraction;
import com.twitter.entity.TweetInteractionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TweetInteractionRepo extends JpaRepository<TweetInteraction, TweetInteractionId> {

    interface InteractionRow {
        Long getTweetId();
        LocalDateTime getCreatedAt();
    }

    @Query("select i from TweetInteraction i where i.id.userId = :userId and i.id.tweetId in :tweetIds")
    List<TweetInteraction> findByUserIdAndTweetIds(@Param("userId") String userId,
                                                   @Param("tweetIds") Collection<Long> tweetIds);

    @Modifying
    @Query(value = "INSERT IGNORE INTO tweet_interaction (tweet_id, user_id, type, created_at) " +
            "VALUES (:tweetId, :userId, :type, :createdAt)", nativeQuery = true)
    int insertIgnore(@Param("tweetId") Long tweetId,
                     @Param("userId") String userId,
                     @Param("type") int type,
                     @Param("createdAt") LocalDateTime createdAt);

    @Modifying
    @Query("delete from TweetInteraction i where i.id.tweetId = :tweetId " +
            "and i.id.userId = :userId and i.id.type = :type")
    int deleteOne(@Param("tweetId") Long tweetId,
                  @Param("userId") String userId,
                  @Param("type") byte type);

    @Query(value = "SELECT tweet_id AS tweetId, created_at AS createdAt " +
            "FROM tweet_interaction WHERE user_id = :userId AND type = :type " +
            "ORDER BY created_at DESC, tweet_id DESC LIMIT :limit", nativeQuery = true)
    List<InteractionRow> findInteractionPageFirst(@Param("userId") String userId,
                                                  @Param("type") int type,
                                                  @Param("limit") int limit);

    @Query(value = "SELECT tweet_id AS tweetId, created_at AS createdAt " +
            "FROM tweet_interaction WHERE user_id = :userId AND type = :type " +
            "AND (created_at < :cursorTime OR (created_at = :cursorTime AND tweet_id < :cursorId)) " +
            "ORDER BY created_at DESC, tweet_id DESC LIMIT :limit", nativeQuery = true)
    List<InteractionRow> findInteractionPageAfter(@Param("userId") String userId,
                                                  @Param("type") int type,
                                                  @Param("cursorTime") LocalDateTime cursorTime,
                                                  @Param("cursorId") long cursorId,
                                                  @Param("limit") int limit);
}
