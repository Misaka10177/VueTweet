package com.twitter.service;

import com.twitter.dto.TrendDto;
import com.twitter.entity.Trend;
import com.twitter.repo.TrendRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrendService {
    private final TrendRepo trendRepo;

    public TrendService(TrendRepo trendRepo) {
        this.trendRepo = trendRepo;
    }

    @Transactional(readOnly = true)
    public List<TrendDto> list() {
        List<TrendDto> result = new ArrayList<>();
        for (Trend trend : trendRepo.findAllByOrderByIdAsc()) {
            result.add(new TrendDto(trend.getCategory(), trend.getTopic()));
        }
        return result;
    }
}
