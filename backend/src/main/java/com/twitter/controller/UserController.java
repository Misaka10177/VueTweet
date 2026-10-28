package com.twitter.controller;

import com.twitter.dto.TweetPageDto;
import com.twitter.dto.UserSummaryDto;
import com.twitter.security.AuthPrincipal;
import com.twitter.service.TweetService;
import com.twitter.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final TweetService tweetService;

    public UserController(UserService userService, TweetService tweetService) {
        this.userService = userService;
        this.tweetService = tweetService;
    }

    @GetMapping("/users")
    public List<UserSummaryDto> getUsers() {
        return userService.listUsers();
    }

    @GetMapping("/users/{userId}/tweets")
    public TweetPageDto getUserTweets(
            @PathVariable String userId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getUserTweets(userId, userId(principal), cursor, limit);
    }

    @GetMapping("/users/{userId}/replies")
    public TweetPageDto getUserReplies(
            @PathVariable String userId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return tweetService.getUserReplies(userId, userId(principal), cursor, limit);
    }

    @PostMapping("/checkUser")
    public Map<String, String> checkUser(@RequestBody Map<String, String> body) {
        Map<String, String> result = new HashMap<>();
        String userName = body.get("username");
        if (userName == null || userName.trim().isEmpty()) {
            result.put("status", "error");
            result.put("message", "用户名不能为空");
            return result;
        }
        if (userService.exists(userName.trim())) {
            result.put("status", "success");
        } else {
            result.put("status", "error");
            result.put("message", "用户 " + userName.trim() + " 不存在");
        }
        return result;
    }

    private String userId(AuthPrincipal principal) {
        return principal == null ? null : principal.getUserId();
    }
}
