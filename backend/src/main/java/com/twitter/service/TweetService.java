package com.twitter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.dto.AuthorDto;
import com.twitter.dto.InteractionDto;
import com.twitter.dto.ReplyResult;
import com.twitter.dto.ToggleResult;
import com.twitter.dto.TweetDto;
import com.twitter.entity.Tweet;
import com.twitter.entity.TweetInteraction;
import com.twitter.entity.User;
import com.twitter.exception.ApiException;
import com.twitter.repo.TweetInteractionRepo;
import com.twitter.repo.TweetRepo;
import com.twitter.repo.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private static final int MAX_TEXT_LENGTH = 280;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;
    private static final Set<String> INTERACTION_TYPES = new HashSet<>(
            Arrays.asList("upvote", "transpond", "bookmark"));

    private final TweetRepo tweetRepo;
    private final UserRepo userRepo;
    private final TweetInteractionRepo interactionRepo;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    public TweetService(TweetRepo tweetRepo, UserRepo userRepo,
                        TweetInteractionRepo interactionRepo,
                        FileStorageService fileStorageService,
                        ObjectMapper objectMapper) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.interactionRepo = interactionRepo;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<TweetDto> getFeed(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), normalizeSize(size));
        Page<Tweet> result = tweetRepo.findByReplyToIdIsNullOrderByCreatedAtDesc(pageable);
        List<Tweet> tweets = result.getContent();
        Map<Long, Set<String>> flags = loadInteractions(userId, tweets);
        List<TweetDto> dtos = new ArrayList<>();
        for (Tweet tweet : tweets) {
            dtos.add(toDto(tweet, flags.get(tweet.getId()), userId));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public TweetDto getTweet(Long tweetId, String userId) {
        Tweet tweet = tweetRepo.findWithAuthorById(tweetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND", "帖子不存在"));
        Map<Long, Set<String>> flags = loadInteractions(userId, Collections.singletonList(tweet));
        TweetDto dto = toDto(tweet, flags.get(tweet.getId()), userId);
        if (tweet.getReplyToId() != null) {
            tweetRepo.findWithAuthorById(tweet.getReplyToId())
                    .ifPresent(parent -> dto.setReplyTo(toDto(parent, null, null)));
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TweetDto> getReplies(Long tweetId, String userId) {
        if (!tweetRepo.existsById(tweetId)) {
            return Collections.emptyList();
        }
        List<Tweet> replies = tweetRepo.findByReplyToIdOrderByCreatedAtAsc(tweetId);
        Map<Long, Set<String>> flags = loadInteractions(userId, replies);
        List<TweetDto> dtos = new ArrayList<>();
        for (Tweet reply : replies) {
            dtos.add(toDto(reply, flags.get(reply.getId()), userId));
        }
        return dtos;
    }

    @Transactional
    public TweetDto create(String userId, String text, MultipartFile[] images, Long replyToId) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "请先登录"));
        String content = text == null ? "" : text.trim();
        if (content.isEmpty() && (images == null || images.length == 0)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_TWEET", "内容不能为空");
        }
        if (content.length() > MAX_TEXT_LENGTH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "TEXT_TOO_LONG", "最多输入 " + MAX_TEXT_LENGTH + " 个字符");
        }

        Tweet parent = null;
        if (replyToId != null) {
            parent = tweetRepo.findWithAuthorById(replyToId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND", "被回复的帖子不存在"));
        }

        List<String> imagePaths = fileStorageService.store(images);
        if (content.isEmpty() && imagePaths.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_TWEET", "内容不能为空");
        }

        Tweet tweet = new Tweet();
        tweet.setAuthor(author);
        tweet.setReplyToId(replyToId);
        tweet.setText(content);
        tweet.setCreatedAt(java.time.LocalDateTime.now());
        try {
            tweet.setImages(objectMapper.writeValueAsString(imagePaths));
            Tweet saved = tweetRepo.save(tweet);
            if (parent != null) {
                tweetRepo.incrementReplyCount(parent.getId());
            }
            return toDto(saved, Collections.emptySet(), userId);
        } catch (ApiException ex) {
            fileStorageService.delete(imagePaths);
            throw ex;
        } catch (Exception ex) {
            fileStorageService.delete(imagePaths);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "CREATE_TWEET_FAILED", "发布失败，请稍后重试");
        }
    }

    @Transactional
    public ReplyResult addReply(Long tweetId, String userId, String text) {
        TweetDto reply = create(userId, text, null, tweetId);
        return new ReplyResult("success", reply.getId());
    }

    @Transactional
    public ToggleResult toggleInteraction(Long tweetId, String userId, String type) {
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "请先登录");
        }
        if (!INTERACTION_TYPES.contains(type)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_INTERACTION", "不支持的互动类型");
        }
        Tweet tweet = tweetRepo.findWithAuthorById(tweetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND", "帖子不存在"));

        Optional<TweetInteraction> existing = interactionRepo.findByTweetIdAndUserIdAndType(tweetId, userId, type);
        boolean active;
        if (existing.isPresent()) {
            interactionRepo.delete(existing.get());
            active = false;
        } else {
            TweetInteraction interaction = new TweetInteraction();
            interaction.setTweetId(tweetId);
            interaction.setUserId(userId);
            interaction.setType(type);
            interaction.setCreatedAt(java.time.LocalDateTime.now());
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
                throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_INTERACTION", "不支持的互动类型");
        }
        tweetRepo.save(tweet);
        return new ToggleResult("success", active, count);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private Map<Long, Set<String>> loadInteractions(String userId, Collection<Tweet> tweets) {
        Map<Long, Set<String>> result = new HashMap<>();
        if (userId == null || tweets == null || tweets.isEmpty()) {
            return result;
        }
        List<Long> ids = tweets.stream().map(Tweet::getId).collect(Collectors.toList());
        List<TweetInteraction> interactions = interactionRepo.findByUserIdAndTweetIdIn(userId, ids);
        for (TweetInteraction interaction : interactions) {
            Set<String> types = result.get(interaction.getTweetId());
            if (types == null) {
                types = new HashSet<>();
                result.put(interaction.getTweetId(), types);
            }
            types.add(interaction.getType());
        }
        return result;
    }

    private TweetDto toDto(Tweet tweet, Set<String> flags, String currentUserId) {
        User author = tweet.getAuthor();
        AuthorDto authorDto = author == null ? null
                : new AuthorDto(author.getId(), author.getName(), author.getProfilePhoto());
        InteractionDto interaction = new InteractionDto(
                tweet.getReply(), tweet.getTranspond(), tweet.getUpvote(),
                tweet.getViewCount(), tweet.getBookmark());
        TweetDto dto = new TweetDto(
                String.valueOf(tweet.getId()), authorDto, tweet.getText(),
                parseImages(tweet.getImages()), tweet.getCreatedAt(), interaction);
        if (currentUserId != null) {
            dto.setMyUpvote(flags != null && flags.contains("upvote"));
            dto.setMyTranspond(flags != null && flags.contains("transpond"));
            dto.setMyBookmark(flags != null && flags.contains("bookmark"));
        }
        return dto;
    }

    private List<String> parseImages(String images) {
        if (images == null || images.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(images, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
