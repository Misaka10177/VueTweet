package com.twitter.controller;

import com.twitter.entity.Tweet;
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
            map.put("text", t.getText());
            map.put("images", t.getImages());
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
