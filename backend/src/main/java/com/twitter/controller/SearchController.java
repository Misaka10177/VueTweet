package com.twitter.controller;

import com.twitter.dto.FollowUserDto;
import com.twitter.dto.SearchOverviewDto;
import com.twitter.dto.TrendDto;
import com.twitter.dto.TweetPageDto;
import com.twitter.security.AuthPrincipal;
import com.twitter.service.SearchService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public SearchOverviewDto overview(
            @RequestParam(value = "q", defaultValue = "") String query,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return searchService.overview(query, principal.getUserId());
    }

    @GetMapping("/search/users")
    public List<FollowUserDto> users(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return searchService.users(query, principal.getUserId(), limit);
    }

    @GetMapping("/search/tweets")
    public TweetPageDto tweets(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return searchService.tweets(query, principal.getUserId(), cursor, limit);
    }

    @GetMapping("/search/topics")
    public List<TrendDto> topics(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return searchService.topics(query, limit);
    }
}
