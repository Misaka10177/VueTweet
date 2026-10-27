package com.twitter.repo;

import com.twitter.entity.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TweetRepo extends JpaRepository<Tweet, Long> {

    @EntityGraph(attributePaths = "author")
    Page<Tweet> findByReplyToIdIsNullOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    List<Tweet> findByReplyToIdOrderByCreatedAtAsc(Long replyToId);

    @EntityGraph(attributePaths = "author")
    @Query("select t from Tweet t where t.id = :id")
    Optional<Tweet> findWithAuthorById(@Param("id") Long id);

    long countByReplyToId(Long replyToId);

    @Modifying
    @Query("update Tweet t set t.reply = t.reply + 1 where t.id = :id")
    int incrementReplyCount(@Param("id") Long id);
}
