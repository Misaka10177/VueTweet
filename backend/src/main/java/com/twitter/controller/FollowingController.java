package com.twitter.controller;

import com.twitter.security.AuthPrincipal;
import com.twitter.service.FollowService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FollowingController {
    private final FollowService followService;

    public FollowingController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/following")
    public Map<String, List<String>> getFollowing(@AuthenticationPrincipal AuthPrincipal principal) {
        Map<String, List<String>> result = new HashMap<>();
        result.put("list", followService.following(principal.getUserId()));
        return result;
    }

    @PutMapping("/following")
    public Map<String, String> addFollowing(@RequestBody String targetId,
                                            @AuthenticationPrincipal AuthPrincipal principal) {
        Map<String, String> result = new HashMap<>();
        if (targetId == null || targetId.trim().isEmpty()) {
            result.put("status", "error");
            return result;
        }
        followService.follow(principal.getUserId(), targetId.trim());
        result.put("status", "success");
        return result;
    }

    @DeleteMapping("/following/{targetId}")
    public Map<String, String> removeFollowing(@PathVariable String targetId,
                                               @AuthenticationPrincipal AuthPrincipal principal) {
        followService.unfollow(principal.getUserId(), targetId);
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        return result;
    }
}
