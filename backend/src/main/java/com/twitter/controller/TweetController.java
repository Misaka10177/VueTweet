package com.twitter.controller;

import com.twitter.dto.ActionStateDto;
import com.twitter.dto.TweetDto;
import com.twitter.dto.TweetPageDto;
import com.twitter.model.InteractionType;
import com.twitter.security.AuthPrincipal;
import com.twitter.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/tweets")
    public TweetPageDto getTweets(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "feed", required = false) String feed,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getFeed(userId(principal), cursor, limit,
                "following".equalsIgnoreCase(feed));
    }

    @GetMapping("/tweets/{tweetId}")
    public TweetDto getTweet(@PathVariable Long tweetId,
                             @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getTweet(tweetId, userId(principal));
    }

    @GetMapping("/tweets/{tweetId}/replies")
    public TweetPageDto getReplies(@PathVariable Long tweetId,
                                   @RequestParam(value = "cursor", required = false) String cursor,
                                   @RequestParam(value = "limit", defaultValue = "20") int limit,
                                   @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getReplies(tweetId, userId(principal), cursor, limit);
    }

    @PostMapping(value = "/tweets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TweetDto> createTweet(
            @RequestParam(value = "text", required = false, defaultValue = "") String text,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "replyToId", required = false) Long replyToId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        TweetDto created = tweetService.create(requireUserId(principal), text, images, replyToId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/tweets/{tweetId}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long tweetId,
                                            @AuthenticationPrincipal AuthPrincipal principal) {
        tweetService.delete(tweetId, requireUserId(principal));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tweets/{tweetId}/like")
    public ActionStateDto like(@PathVariable Long tweetId,
                               @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.setInteraction(tweetId, requireUserId(principal), InteractionType.LIKE, true);
    }

    @DeleteMapping("/tweets/{tweetId}/like")
    public ActionStateDto unlike(@PathVariable Long tweetId,
                                 @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.setInteraction(tweetId, requireUserId(principal), InteractionType.LIKE, false);
    }

    @PutMapping("/tweets/{tweetId}/repost")
    public ActionStateDto repost(@PathVariable Long tweetId,
                                 @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.setInteraction(tweetId, requireUserId(principal), InteractionType.REPOST, true);
    }

    @DeleteMapping("/tweets/{tweetId}/repost")
    public ActionStateDto unrepost(@PathVariable Long tweetId,
                                   @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.setInteraction(tweetId, requireUserId(principal), InteractionType.REPOST, false);
    }

    @PutMapping("/tweets/{tweetId}/bookmark")
    public ActionStateDto bookmark(@PathVariable Long tweetId,
                                   @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.setInteraction(tweetId, requireUserId(principal), InteractionType.BOOKMARK, true);
    }

    @DeleteMapping("/tweets/{tweetId}/bookmark")
    public ActionStateDto unbookmark(@PathVariable Long tweetId,
                                     @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.setInteraction(tweetId, requireUserId(principal), InteractionType.BOOKMARK, false);
    }

    private String userId(AuthPrincipal principal) {
        return principal == null ? null : principal.getUserId();
    }

    private String requireUserId(AuthPrincipal principal) {
        if (principal == null) {
            throw new com.twitter.exception.ApiException(HttpStatus.UNAUTHORIZED,
                    "UNAUTHORIZED", "请先登录");
        }
        return principal.getUserId();
    }
}
