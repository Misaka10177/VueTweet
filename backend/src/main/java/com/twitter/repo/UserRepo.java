package com.twitter.repo;

import com.twitter.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, String> {

    @Query("select u from User u where u.id <> :userId and not exists " +
            "(select f.id from Follow f where f.followerId = :userId and f.followeeId = u.id) " +
            "order by u.id asc")
    List<User> findSuggestions(@Param("userId") String userId, Pageable pageable);
}
