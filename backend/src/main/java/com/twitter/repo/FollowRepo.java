package com.twitter.repo;

import com.twitter.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepo extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowerIdOrderByCreatedAtDesc(String followerId);

    Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId);

    long countByFolloweeId(String followeeId);

    long countByFollowerId(String followerId);
}
