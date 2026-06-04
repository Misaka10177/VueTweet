package com.twitter.controller;

import com.twitter.entity.Tweet;
import com.twitter.entity.User;
import com.twitter.repo.TweetRepo;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TweetController {

    private final TweetRepo tweetRepo;

    public TweetController(TweetRepo tweetRepo) {
        this.tweetRepo = tweetRepo;
    }

    @GetMapping("/tweet")
    public List<Map<String, Object>> getTweets() {
        List<Tweet> tweets = tweetRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tweet t : tweets) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", String.valueOf(t.getId()));
            // author
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
}
