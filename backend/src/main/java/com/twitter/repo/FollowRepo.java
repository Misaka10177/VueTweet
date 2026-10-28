package com.twitter.repo;

import com.twitter.entity.Follow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface FollowRepo extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFolloweeId(String followerId, String followeeId);

    @Query("select f.followeeId from Follow f where f.followerId = :followerId")
    List<String> findFolloweeIdsByFollowerId(@Param("followerId") String followerId);

    @Query("select f.followeeId from Follow f where f.followerId = :followerId " +
            "and f.followeeId in :followeeIds")
    List<String> findFolloweeIdsAmong(@Param("followerId") String followerId,
                                      @Param("followeeIds") Collection<String> followeeIds);

    @Query("select f from Follow f where f.followerId = :userId " +
            "order by f.createdAt desc, f.id desc")
    List<Follow> findFollowingFirst(@Param("userId") String userId, Pageable pageable);

    @Query("select f from Follow f where f.followerId = :userId " +
            "and (f.createdAt < :cursorTime or (f.createdAt = :cursorTime and f.id < :cursorId)) " +
            "order by f.createdAt desc, f.id desc")
    List<Follow> findFollowingAfter(@Param("userId") String userId,
                                    @Param("cursorTime") LocalDateTime cursorTime,
                                    @Param("cursorId") long cursorId,
                                    Pageable pageable);

    @Query("select f from Follow f where f.followeeId = :userId " +
            "order by f.createdAt desc, f.id desc")
    List<Follow> findFollowersFirst(@Param("userId") String userId, Pageable pageable);

    @Query("select f from Follow f where f.followeeId = :userId " +
            "and (f.createdAt < :cursorTime or (f.createdAt = :cursorTime and f.id < :cursorId)) " +
            "order by f.createdAt desc, f.id desc")
    List<Follow> findFollowersAfter(@Param("userId") String userId,
                                    @Param("cursorTime") LocalDateTime cursorTime,
                                    @Param("cursorId") long cursorId,
                                    Pageable pageable);

    /**
     * INSERT IGNORE keeps follow/unfollow idempotent and avoids a check-then-insert race.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT IGNORE INTO follow (follower_id, followee_id, created_at) " +
            "VALUES (:followerId, :followeeId, :createdAt)", nativeQuery = true)
    int insertIgnore(@Param("followerId") String followerId,
                     @Param("followeeId") String followeeId,
                     @Param("createdAt") LocalDateTime createdAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Follow f where f.followerId = :followerId and f.followeeId = :followeeId")
    int deleteOne(@Param("followerId") String followerId,
                  @Param("followeeId") String followeeId);

    long countByFolloweeId(String followeeId);

    long countByFollowerId(String followerId);
}
