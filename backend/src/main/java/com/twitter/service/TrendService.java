package com.twitter.service;

import com.twitter.dto.TrendDto;
import com.twitter.entity.Trend;
import com.twitter.repo.TrendRepo;
import com.twitter.util.SearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TrendService {
    private static final int MAX_SEARCH_RESULTS = 50;

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

    @Transactional(readOnly = true)
    public List<TrendDto> search(String rawQuery, int limit) {
        String query = SearchQuery.normalize(rawQuery);
        if (query.isEmpty()) {
            return Collections.emptyList();
        }
        int size = limit <= 0 ? 10 : Math.min(limit, MAX_SEARCH_RESULTS);
        List<Trend> rows = trendRepo.search(SearchQuery.likePattern(query), PageRequest.of(0, size));
        List<TrendDto> result = new ArrayList<>();
        for (Trend trend : rows) {
            result.add(new TrendDto(trend.getCategory(), trend.getTopic()));
        }
        return result;
    }
}
