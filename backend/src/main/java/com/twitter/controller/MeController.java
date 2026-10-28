package com.twitter.controller;

import com.twitter.dto.MeDto;
import com.twitter.dto.TweetPageDto;
import com.twitter.security.AuthPrincipal;
import com.twitter.service.TweetService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {
    private final TweetService tweetService;

    public MeController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/me")
    public MeDto me(@AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getMe(principal.getUserId());
    }

    @GetMapping("/me/bookmarks")
    public TweetPageDto bookmarks(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getBookmarks(principal.getUserId(), cursor, limit);
    }

    @GetMapping("/me/likes")
    public TweetPageDto likes(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getLikes(principal.getUserId(), cursor, limit);
    }
}
