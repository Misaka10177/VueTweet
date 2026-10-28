package com.twitter.repo;

import com.twitter.entity.Trend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrendRepo extends JpaRepository<Trend, Long> {
    List<Trend> findAllByOrderByIdAsc();

    @Query("select t from Trend t where lower(t.topic) like :pattern escape '!' " +
            "or lower(t.category) like :pattern escape '!' order by t.id asc")
    List<Trend> search(@Param("pattern") String pattern, Pageable pageable);
}
