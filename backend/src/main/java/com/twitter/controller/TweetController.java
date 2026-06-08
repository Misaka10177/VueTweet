package com.twitter.controller;

import com.twitter.entity.Tweet;
import com.twitter.entity.User;
import com.twitter.repo.TweetRepo;
import com.twitter.repo.UserRepo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public List<Map<String, Object>> getReplies(@PathVariable Long tweetId, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute("userId");
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            return Collections.emptyList();
        }
        List<Tweet> replies = tweetRepo.findByReplyToOrderByCreatedAtDesc(tweetOpt.get());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tweet t : replies) {
            result.add(tweetToMap(t, currentUserId));
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
        Tweet original = tweetOpt.get();
        original.setReply(original.getReply() + 1);
        tweetRepo.save(original);
        result.put("status", "success");
        result.put("id", String.valueOf(reply.getId()));
        return result;
    }

    @PostMapping("/tweet/{tweetId}/upvote")
    public Map<String, Object> toggleUpvote(@PathVariable Long tweetId, HttpServletRequest request) {
        return toggleUserList(tweetId, (String) request.getAttribute("userId"), "upvote");
    }

    @PostMapping("/tweet/{tweetId}/transpond")
    public Map<String, Object> toggleTranspond(@PathVariable Long tweetId, HttpServletRequest request) {
        return toggleUserList(tweetId, (String) request.getAttribute("userId"), "transpond");
    }

    @PostMapping("/tweet/{tweetId}/bookmark")
    public Map<String, Object> toggleBookmark(@PathVariable Long tweetId, HttpServletRequest request) {
        return toggleUserList(tweetId, (String) request.getAttribute("userId"), "bookmark");
    }

    private Map<String, Object> toggleUserList(Long tweetId, String userId, String type) {
        Map<String, Object> result = new LinkedHashMap<>();
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent() || userId == null) {
            result.put("status", "error");
            return result;
        }
        Tweet tweet = tweetOpt.get();
        String users;
        int count;
        switch (type) {
            case "upvote":
                users = tweet.getUpvoteUsers();
                count = tweet.getUpvote();
                break;
            case "transpond":
                users = tweet.getTranspondUsers();
                count = tweet.getTranspond();
                break;
            case "bookmark":
                users = tweet.getBookmarkUsers();
                count = tweet.getBookmark();
                break;
            default:
                result.put("status", "error");
                return result;
        }
        Set<String> userSet = new HashSet<>();
        if (users != null && !users.isEmpty()) {
            userSet.addAll(Arrays.asList(users.split(",")));
        }
        boolean active;
        if (userSet.contains(userId)) {
            userSet.remove(userId);
            count--;
            active = false;
        } else {
            userSet.add(userId);
            count++;
            active = true;
        }
        String newUsers = String.join(",", userSet);
        switch (type) {
            case "upvote":
                tweet.setUpvoteUsers(newUsers);
                tweet.setUpvote(count);
                break;
            case "transpond":
                tweet.setTranspondUsers(newUsers);
                tweet.setTranspond(count);
                break;
            case "bookmark":
                tweet.setBookmarkUsers(newUsers);
                tweet.setBookmark(count);
                break;
        }
        tweetRepo.save(tweet);
        result.put("status", "success");
        result.put("active", active);
        result.put("count", count);
        return result;
    }

    @GetMapping("/tweet/{tweetId}")
    public Map<String, Object> getTweet(@PathVariable Long tweetId, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute("userId");
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            return Collections.emptyMap();
        }
        return tweetToMap(tweetOpt.get(), currentUserId);
    }

    @GetMapping("/tweet")
    public List<Map<String, Object>> getTweets(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute("userId");
        List<Tweet> tweets = tweetRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tweet t : tweets) {
            result.add(tweetToMap(t, currentUserId));
        }
        return result;
    }

    private Map<String, Object> tweetToMap(Tweet t, String currentUserId) {
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
        Tweet replyTo = t.getReplyTo();
        if (replyTo != null) {
            map.put("replyTo", tweetToMap(replyTo, currentUserId));
        }
        Map<String, Integer> interaction = new LinkedHashMap<>();
        interaction.put("reply", t.getReply());
        interaction.put("transpond", t.getTranspond());
        interaction.put("upvote", t.getUpvote());
        interaction.put("view", t.getViewCount());
        interaction.put("bookmark", t.getBookmark());
        map.put("interaction", interaction);
        // myXxx 布尔值
        if (currentUserId != null) {
            map.put("myUpvote", containsUser(t.getUpvoteUsers(), currentUserId));
            map.put("myTranspond", containsUser(t.getTranspondUsers(), currentUserId));
            map.put("myBookmark", containsUser(t.getBookmarkUsers(), currentUserId));
        }
        return map;
    }

    private boolean containsUser(String users, String userId) {
        if (users == null || users.isEmpty()) return false;
        return Arrays.asList(users.split(",")).contains(userId);
    }
}
