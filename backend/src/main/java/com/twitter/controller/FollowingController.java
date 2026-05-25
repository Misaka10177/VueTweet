package com.twitter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FollowingController {

    private final List<String> followingList = new ArrayList<>();

    @GetMapping("/following")
    public Map<String, List<String>> getFollowing() {
        Map<String, List<String>> result = new HashMap<>();
        result.put("list", followingList);
        return result;
    }

    @PutMapping("/following")
    public ResponseEntity<Map<String, String>> addFollowing(@RequestBody String userId) {
        Map<String, String> result = new HashMap<>();
        if (userId == null || userId.trim().isEmpty()) {
            result.put("status", "error");
            return ResponseEntity.ok(result);
        }
        userId = userId.trim();
        if (userId.length() >= 20) {
            result.put("status", "error");
            return ResponseEntity.ok(result);
        }
        if (!followingList.contains(userId)) {
            followingList.add(userId);
        }
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }
}
