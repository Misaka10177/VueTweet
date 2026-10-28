package com.twitter.service;

import com.twitter.dto.FollowUserDto;
import com.twitter.dto.SearchOverviewDto;
import com.twitter.dto.TrendDto;
import com.twitter.dto.TweetPageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SearchService {
    private static final int OVERVIEW_USERS = 3;
    private static final int OVERVIEW_TWEETS = 5;
    private static final int OVERVIEW_TOPICS = 5;
    private static final int MAX_RESULTS = 50;

    private final FollowService followService;
    private final TweetService tweetService;
    private final TrendService trendService;

    public SearchService(FollowService followService, TweetService tweetService,
                         TrendService trendService) {
        this.followService = followService;
        this.tweetService = tweetService;
        this.trendService = trendService;
    }

    @Transactional(readOnly = true)
    public SearchOverviewDto overview(String rawQuery, String viewerId) {
        String query = rawQuery == null ? "" : rawQuery.trim();
        if (query.isEmpty()) {
            return new SearchOverviewDto("", Collections.emptyList(),
                    new TweetPageDto(Collections.emptyList(), null, false),
                    Collections.emptyList());
        }
        return new SearchOverviewDto(
                query,
                followService.searchUsers(query, viewerId, OVERVIEW_USERS),
                tweetService.searchTweets(query, viewerId, null, OVERVIEW_TWEETS),
                trendService.search(query, OVERVIEW_TOPICS));
    }

    @Transactional(readOnly = true)
    public List<FollowUserDto> users(String query, String viewerId, int limit) {
        return followService.searchUsers(query, viewerId, normalizeLimit(limit));
    }

    @Transactional(readOnly = true)
    public TweetPageDto tweets(String query, String viewerId, String cursor, int limit) {
        return tweetService.searchTweets(query, viewerId, cursor, normalizeLimit(limit));
    }

    @Transactional(readOnly = true)
    public List<TrendDto> topics(String query, int limit) {
        return trendService.search(query, normalizeLimit(limit));
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 20;
        }
        return Math.min(limit, MAX_RESULTS);
    }
}
