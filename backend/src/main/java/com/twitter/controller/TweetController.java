package com.twitter.controller;

import com.twitter.entity.Tweet;
import com.twitter.entity.User;
import com.twitter.repo.TweetRepo;
import com.twitter.repo.UserRepo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class TweetController {

    private final TweetRepo tweetRepo;
    private final UserRepo userRepo;

    public TweetController(TweetRepo tweetRepo, UserRepo userRepo) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/tweet/{tweetId}/replies")
    public List<Map<String, Object>> getReplies(@PathVariable Long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            return Collections.emptyList();
        }
        List<Tweet> replies = tweetRepo.findByReplyToOrderByCreatedAtDesc(tweetOpt.get());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tweet t : replies) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", String.valueOf(t.getId()));
            User author = t.getAuthor();
            if (author != null) {
                Map<String, String> authorMap = new LinkedHashMap<>();
                authorMap.put("id", author.getId());
                authorMap.put("name", author.getName());
                authorMap.put("profile_photo", author.getProfilePhoto());
                map.put("author", authorMap);
            }
            map.put("text", t.getText());
            map.put("images", t.getImages());
            map.put("publishTime", t.getCreatedAt());
            Map<String, Integer> interaction = new LinkedHashMap<>();
            interaction.put("reply", t.getReply());
            interaction.put("transpond", t.getTranspond());
            interaction.put("upvote", t.getUpvote());
            interaction.put("view", t.getViewCount());
            map.put("interaction", interaction);
            result.add(map);
        }
        return result;
    }

    @PostMapping("/tweet/{tweetId}/reply")
    public Map<String, Object> addReply(@PathVariable Long tweetId, @RequestBody Map<String, String> body) {
        Map<String, Object> result = new LinkedHashMap<>();
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            result.put("status", "error");
            result.put("message", "Tweet not found");
            return result;
        }
        String userId = body.get("userId");
        String text = body.get("text");
        if (userId == null || text == null || text.trim().isEmpty()) {
            result.put("status", "error");
            result.put("message", "Missing userId or text");
            return result;
        }
        User author = userRepo.findById(userId).orElse(null);
        if (author == null) {
            result.put("status", "error");
            result.put("message", "User not found");
            return result;
        }
        Tweet reply = new Tweet();
        reply.setReplyTo(tweetOpt.get());
        reply.setAuthor(author);
        reply.setText(text);
        reply.setCreatedAt(LocalDateTime.now());
        tweetRepo.save(reply);
        // 更新原推文的 reply 计数
        Tweet original = tweetOpt.get();
        original.setReply(original.getReply() + 1);
        tweetRepo.save(original);
        result.put("status", "success");
        result.put("id", String.valueOf(reply.getId()));
        return result;
    }

    @GetMapping("/tweet/{tweetId}")
    public Map<String, Object> getTweet(@PathVariable Long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            return Collections.emptyMap();
        }
        return tweetToMap(tweetOpt.get());
    }

    @GetMapping("/tweet")
    public List<Map<String, Object>> getTweets() {
        List<Tweet> tweets = tweetRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tweet t : tweets) {
            result.add(tweetToMap(t));
        }
        return result;
    }

    private Map<String, Object> tweetToMap(Tweet t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", String.valueOf(t.getId()));
        User author = t.getAuthor();
        if (author != null) {
            Map<String, String> authorMap = new LinkedHashMap<>();
            authorMap.put("id", author.getId());
            authorMap.put("name", author.getName());
            authorMap.put("profile_photo", author.getProfilePhoto());
            map.put("author", authorMap);
        }
        map.put("text", t.getText());
        map.put("images", t.getImages());
        map.put("publishTime", t.getCreatedAt());
        // replyTo
        Tweet replyTo = t.getReplyTo();
        if (replyTo != null) {
            map.put("replyTo", tweetToMap(replyTo));
        }
        Map<String, Integer> interaction = new LinkedHashMap<>();
        interaction.put("reply", t.getReply());
        interaction.put("transpond", t.getTranspond());
        interaction.put("upvote", t.getUpvote());
        interaction.put("view", t.getViewCount());
        map.put("interaction", interaction);
        return map;
    }
}
