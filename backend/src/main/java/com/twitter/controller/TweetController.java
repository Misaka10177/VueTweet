package com.twitter.controller;

import com.twitter.dto.ReplyResult;
import com.twitter.dto.ToggleResult;
import com.twitter.dto.TweetDto;
import com.twitter.security.AuthPrincipal;
import com.twitter.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/tweet")
    public List<TweetDto> getTweets(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getFeed(principal.getUserId(), page, size);
    }

    @GetMapping("/tweet/{tweetId}")
    public TweetDto getTweet(@PathVariable Long tweetId, @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getTweet(tweetId, principal.getUserId());
    }

    @GetMapping("/tweet/{tweetId}/replies")
    public List<TweetDto> getReplies(@PathVariable Long tweetId, @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getReplies(tweetId, principal.getUserId());
    }

    @PostMapping(value = "/tweet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TweetDto> createTweet(
            @RequestParam(value = "text", required = false, defaultValue = "") String text,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "replyToId", required = false) Long replyToId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        TweetDto created = tweetService.create(principal.getUserId(), text, images, replyToId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/tweet/{tweetId}/reply")
    public ReplyResult addReply(@PathVariable Long tweetId,
                                @RequestBody Map<String, String> body,
                                @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.addReply(tweetId, principal.getUserId(), body.get("text"));
    }

    @PostMapping("/tweet/{tweetId}/upvote")
    public ToggleResult toggleUpvote(@PathVariable Long tweetId, @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.toggleInteraction(tweetId, principal.getUserId(), "upvote");
    }

    @PostMapping("/tweet/{tweetId}/transpond")
    public ToggleResult toggleTranspond(@PathVariable Long tweetId, @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.toggleInteraction(tweetId, principal.getUserId(), "transpond");
    }

    @PostMapping("/tweet/{tweetId}/bookmark")
    public ToggleResult toggleBookmark(@PathVariable Long tweetId, @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.toggleInteraction(tweetId, principal.getUserId(), "bookmark");
    }
}
