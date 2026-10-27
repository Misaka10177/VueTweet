package com.twitter.repo;

import com.twitter.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrendRepo extends JpaRepository<Trend, Long> {
    List<Trend> findAllByOrderByIdAsc();
}
