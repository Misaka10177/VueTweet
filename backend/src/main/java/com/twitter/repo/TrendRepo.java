package com.twitter.repo;

import com.twitter.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendRepo extends JpaRepository<Trend, Long> {
}
