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

    @Query("select u from User u where lower(u.id) like :pattern escape '!' " +
            "or lower(u.name) like :pattern escape '!' " +
            "order by case when lower(u.id) = :query then 0 " +
            "when lower(u.name) = :query then 1 else 2 end, u.id asc")
    List<User> search(@Param("query") String query,
                      @Param("pattern") String pattern,
                      Pageable pageable);
}
