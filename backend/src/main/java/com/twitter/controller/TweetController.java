package com.twitter.controller;

import com.twitter.entity.Tweet;
import com.twitter.entity.TweetInteraction;
import com.twitter.entity.TweetReply;
import com.twitter.entity.User;
import com.twitter.repo.TweetInteractionRepo;
import com.twitter.repo.TweetReplyRepo;
import com.twitter.repo.TweetRepo;
import com.twitter.repo.UserRepo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TweetController {

    private final TweetRepo tweetRepo;
    private final UserRepo userRepo;
    private final TweetInteractionRepo interactionRepo;
    private final TweetReplyRepo replyRepo;

    public TweetController(TweetRepo tweetRepo, UserRepo userRepo, TweetInteractionRepo interactionRepo, TweetReplyRepo replyRepo) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.interactionRepo = interactionRepo;
        this.replyRepo = replyRepo;
    }

    @GetMapping("/tweet/{tweetId}/replies")
    public List<Map<String, Object>> getReplies(@PathVariable Long tweetId, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute("userId");
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            return Collections.emptyList();
        }
        List<TweetReply> replyRelations = replyRepo.findByTweetId(tweetId);
        List<Long> replyIds = replyRelations.stream().map(TweetReply::getReplyId).collect(Collectors.toList());
        List<Tweet> replies = tweetRepo.findAllById(replyIds);
        replies.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
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
        reply.setAuthor(author);
        reply.setText(text);
        reply.setCreatedAt(LocalDateTime.now());
        tweetRepo.save(reply);
        TweetReply relation = new TweetReply();
        relation.setTweetId(tweetId);
        relation.setReplyId(reply.getId());
        replyRepo.save(relation);
        Tweet original = tweetOpt.get();
        original.setReply(original.getReply() + 1);
        tweetRepo.save(original);
        result.put("status", "success");
        result.put("id", String.valueOf(reply.getId()));
        return result;
    }

    @PostMapping("/tweet/{tweetId}/upvote")
    public Map<String, Object> toggleUpvote(@PathVariable Long tweetId, HttpServletRequest request) {
        return toggleInteraction(tweetId, (String) request.getAttribute("userId"), "upvote");
    }

    @PostMapping("/tweet/{tweetId}/transpond")
    public Map<String, Object> toggleTranspond(@PathVariable Long tweetId, HttpServletRequest request) {
        return toggleInteraction(tweetId, (String) request.getAttribute("userId"), "transpond");
    }

    @PostMapping("/tweet/{tweetId}/bookmark")
    public Map<String, Object> toggleBookmark(@PathVariable Long tweetId, HttpServletRequest request) {
        return toggleInteraction(tweetId, (String) request.getAttribute("userId"), "bookmark");
    }

    private Map<String, Object> toggleInteraction(Long tweetId, String userId, String type) {
        Map<String, Object> result = new LinkedHashMap<>();
        Optional<Tweet> tweetOpt = tweetRepo.findById(tweetId);
        if (!tweetOpt.isPresent() || userId == null) {
            result.put("status", "error");
            return result;
        }
        Tweet tweet = tweetOpt.get();
        TweetInteraction existing = interactionRepo.findByTweetIdAndUserIdAndType(tweetId, userId, type);
        boolean active;
        if (existing != null) {
            interactionRepo.delete(existing);
            active = false;
        } else {
            TweetInteraction interaction = new TweetInteraction();
            interaction.setTweetId(tweetId);
            interaction.setUserId(userId);
            interaction.setType(type);
            interaction.setCreatedAt(LocalDateTime.now());
            interactionRepo.save(interaction);
            active = true;
        }
        int count;
        switch (type) {
            case "upvote":
                tweet.setUpvote(tweet.getUpvote() + (active ? 1 : -1));
                count = tweet.getUpvote();
                break;
            case "transpond":
                tweet.setTranspond(tweet.getTranspond() + (active ? 1 : -1));
                count = tweet.getTranspond();
                break;
            case "bookmark":
                tweet.setBookmark(tweet.getBookmark() + (active ? 1 : -1));
                count = tweet.getBookmark();
                break;
            default:
                result.put("status", "error");
                return result;
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
        // replyTo: 查关系表找被回复的推文
        TweetReply parentRelation = replyRepo.findByReplyId(t.getId());
        if (parentRelation != null) {
            Optional<Tweet> parentTweet = tweetRepo.findById(parentRelation.getTweetId());
            if (parentTweet.isPresent()) {
                map.put("replyTo", tweetToMap(parentTweet.get(), currentUserId));
            }
        }
        Map<String, Integer> interaction = new LinkedHashMap<>();
        interaction.put("reply", t.getReply());
        interaction.put("transpond", t.getTranspond());
        interaction.put("upvote", t.getUpvote());
        interaction.put("view", t.getViewCount());
        interaction.put("bookmark", t.getBookmark());
        map.put("interaction", interaction);
        if (currentUserId != null) {
            map.put("myUpvote", hasInteraction(t.getId(), currentUserId, "upvote"));
            map.put("myTranspond", hasInteraction(t.getId(), currentUserId, "transpond"));
            map.put("myBookmark", hasInteraction(t.getId(), currentUserId, "bookmark"));
        }
        return map;
    }

    private boolean hasInteraction(Long tweetId, String userId, String type) {
        return interactionRepo.findByTweetIdAndUserIdAndType(tweetId, userId, type) != null;
    }
}
