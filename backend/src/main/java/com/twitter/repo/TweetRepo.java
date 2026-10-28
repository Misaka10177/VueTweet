package com.twitter.repo;

import com.twitter.entity.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TweetRepo extends JpaRepository<Tweet, Long> {

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.id = :id and t.deletedAt is null")
    Optional<Tweet> findActiveWithAuthorById(@Param("id") Long id);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.id in :ids and t.deletedAt is null")
    List<Tweet> findActiveWithAuthorByIdIn(@Param("ids") Collection<Long> ids);

    @Query("select count(t) > 0 from Tweet t where t.id = :id and t.deletedAt is null")
    boolean existsActiveById(@Param("id") Long id);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.replyToId is null and t.deletedAt is null " +
            "order by t.createdAt desc, t.id desc")
    List<Tweet> findFeedFirst(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.replyToId is null and t.deletedAt is null " +
            "and (t.createdAt < :cursorTime or (t.createdAt = :cursorTime and t.id < :cursorId)) " +
            "order by t.createdAt desc, t.id desc")
    List<Tweet> findFeedAfter(@Param("cursorTime") LocalDateTime cursorTime,
                              @Param("cursorId") long cursorId,
                              Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.replyToId is null and t.deletedAt is null " +
            "and t.author.id in :authorIds order by t.createdAt desc, t.id desc")
    List<Tweet> findFollowingFeedFirst(@Param("authorIds") Collection<String> authorIds,
                                       Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.replyToId is null and t.deletedAt is null " +
            "and t.author.id in :authorIds " +
            "and (t.createdAt < :cursorTime or (t.createdAt = :cursorTime and t.id < :cursorId)) " +
            "order by t.createdAt desc, t.id desc")
    List<Tweet> findFollowingFeedAfter(@Param("authorIds") Collection<String> authorIds,
                                       @Param("cursorTime") LocalDateTime cursorTime,
                                       @Param("cursorId") long cursorId,
                                       Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.replyToId = :replyToId and t.deletedAt is null " +
            "order by t.createdAt asc, t.id asc")
    List<Tweet> findRepliesFirst(@Param("replyToId") Long replyToId, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.replyToId = :replyToId and t.deletedAt is null " +
            "and (t.createdAt > :cursorTime or (t.createdAt = :cursorTime and t.id > :cursorId)) " +
            "order by t.createdAt asc, t.id asc")
    List<Tweet> findRepliesAfter(@Param("replyToId") Long replyToId,
                                 @Param("cursorTime") LocalDateTime cursorTime,
                                 @Param("cursorId") long cursorId,
                                 Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.author.id = :authorId and t.replyToId is null " +
            "and t.deletedAt is null order by t.createdAt desc, t.id desc")
    List<Tweet> findUserTweetsFirst(@Param("authorId") String authorId, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.author.id = :authorId and t.replyToId is null " +
            "and t.deletedAt is null " +
            "and (t.createdAt < :cursorTime or (t.createdAt = :cursorTime and t.id < :cursorId)) " +
            "order by t.createdAt desc, t.id desc")
    List<Tweet> findUserTweetsAfter(@Param("authorId") String authorId,
                                    @Param("cursorTime") LocalDateTime cursorTime,
                                    @Param("cursorId") long cursorId,
                                    Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.author.id = :authorId and t.replyToId is not null " +
            "and t.deletedAt is null order by t.createdAt desc, t.id desc")
    List<Tweet> findUserRepliesFirst(@Param("authorId") String authorId, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.author.id = :authorId and t.replyToId is not null " +
            "and t.deletedAt is null " +
            "and (t.createdAt < :cursorTime or (t.createdAt = :cursorTime and t.id < :cursorId)) " +
            "order by t.createdAt desc, t.id desc")
    List<Tweet> findUserRepliesAfter(@Param("authorId") String authorId,
                                     @Param("cursorTime") LocalDateTime cursorTime,
                                     @Param("cursorId") long cursorId,
                                     Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tweet SET reply_count = GREATEST(reply_count + :delta, 0) " +
            "WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
    int changeReplyCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tweet SET repost_count = GREATEST(repost_count + :delta, 0) " +
            "WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
    int changeRepostCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tweet SET like_count = GREATEST(like_count + :delta, 0) " +
            "WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
    int changeLikeCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tweet SET bookmark_count = GREATEST(bookmark_count + :delta, 0) " +
            "WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
    int changeBookmarkCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Tweet t set t.deletedAt = :deletedAt " +
            "where t.id = :id and t.deletedAt is null")
    int softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    long countByAuthorIdAndReplyToIdIsNullAndDeletedAtIsNull(String authorId);
}
