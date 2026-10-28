package com.twitter.service;

import com.twitter.dto.ActionStateDto;
import com.twitter.dto.AuthorDto;
import com.twitter.dto.CompactTweetDto;
import com.twitter.dto.MeDto;
import com.twitter.dto.MediaDto;
import com.twitter.dto.StatsDto;
import com.twitter.dto.TweetDto;
import com.twitter.dto.TweetPageDto;
import com.twitter.dto.ViewerDto;
import com.twitter.entity.Tweet;
import com.twitter.entity.TweetInteraction;
import com.twitter.entity.TweetMedia;
import com.twitter.entity.TweetMediaId;
import com.twitter.entity.User;
import com.twitter.exception.ApiException;
import com.twitter.model.InteractionType;
import com.twitter.repo.FollowRepo;
import com.twitter.repo.TweetInteractionRepo;
import com.twitter.repo.TweetMediaRepo;
import com.twitter.repo.TweetRepo;
import com.twitter.repo.UserRepo;
import com.twitter.util.Cursor;
import com.twitter.util.SearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private static final int MAX_TEXT_LENGTH = 280;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final TweetRepo tweetRepo;
    private final UserRepo userRepo;
    private final TweetInteractionRepo interactionRepo;
    private final TweetMediaRepo mediaRepo;
    private final FollowRepo followRepo;
    private final FileStorageService fileStorageService;

    public TweetService(TweetRepo tweetRepo, UserRepo userRepo,
                        TweetInteractionRepo interactionRepo,
                        TweetMediaRepo mediaRepo,
                        FollowRepo followRepo,
                        FileStorageService fileStorageService) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.interactionRepo = interactionRepo;
        this.mediaRepo = mediaRepo;
        this.followRepo = followRepo;
        this.fileStorageService = fileStorageService;
    }

    // -----------------------------------------------------------------------
    // Reads
    // -----------------------------------------------------------------------

    @Transactional(readOnly = true)
    public TweetPageDto getFeed(String userId, String cursorToken, int limit, boolean followingOnly) {
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        Pageable pageable = PageRequest.of(0, size + 1);

        if (!followingOnly) {
            List<Tweet> rows = cursor == null
                    ? tweetRepo.findFeedFirst(pageable)
                    : tweetRepo.findFeedAfter(cursor.getCreatedAt(), cursor.getId(), pageable);
            return toTweetPage(rows, size, userId);
        }

        // The following timeline contains the viewer's own posts as well.
        List<String> authorIds = new ArrayList<>(followRepo.findFolloweeIdsByFollowerId(userId));
        if (!authorIds.contains(userId)) {
            authorIds.add(userId);
        }
        List<Tweet> rows = cursor == null
                ? tweetRepo.findFollowingFeedFirst(authorIds, pageable)
                : tweetRepo.findFollowingFeedAfter(authorIds, cursor.getCreatedAt(),
                        cursor.getId(), pageable);
        return toTweetPage(rows, size, userId);
    }

    @Transactional(readOnly = true)
    public TweetDto getTweet(Long tweetId, String userId) {
        Tweet tweet = requireTweet(tweetId);
        Map<Long, List<MediaDto>> media = loadMedia(Collections.singletonList(tweet));
        Set<Integer> flags = loadViewerFlags(userId, Collections.singletonList(tweet))
                .getOrDefault(tweet.getId(), Collections.emptySet());

        CompactTweetDto replyTo = null;
        if (tweet.getReplyToId() != null) {
            Tweet parent = tweetRepo.findActiveWithAuthorById(tweet.getReplyToId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "TWEET_NOT_FOUND", "被回复的帖子不存在"));
            Map<Long, List<MediaDto>> parentMedia = loadMedia(Collections.singletonList(parent));
            replyTo = toCompact(parent, parentMedia.getOrDefault(parent.getId(), Collections.emptyList()));
        }
        return toDto(tweet, media.getOrDefault(tweet.getId(), Collections.emptyList()),
                flags, replyTo, userId);
    }

    @Transactional(readOnly = true)
    public TweetPageDto searchTweets(String rawQuery, String userId, String cursorToken, int limit) {
        String query = SearchQuery.normalize(rawQuery);
        if (query.isEmpty()) {
            return new TweetPageDto(Collections.emptyList(), null, false);
        }
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        Pageable pageable = PageRequest.of(0, size + 1);
        String pattern = SearchQuery.likePattern(query);
        List<Tweet> rows = cursor == null
                ? tweetRepo.searchFirst(pattern, pageable)
                : tweetRepo.searchAfter(pattern, cursor.getCreatedAt(), cursor.getId(), pageable);
        return toTweetPage(rows, size, userId);
    }

    @Transactional(readOnly = true)
    public TweetPageDto getReplies(Long tweetId, String userId, String cursorToken, int limit) {
        requireTweet(tweetId);
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Tweet> rows = cursor == null
                ? tweetRepo.findRepliesFirst(tweetId, pageable)
                : tweetRepo.findRepliesAfter(tweetId, cursor.getCreatedAt(), cursor.getId(), pageable);
        return toTweetPage(rows, size, userId);
    }

    @Transactional(readOnly = true)
    public TweetPageDto getUserTweets(String authorId, String userId, String cursorToken, int limit) {
        if (!userRepo.existsById(authorId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在");
        }
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Tweet> rows = cursor == null
                ? tweetRepo.findUserTweetsFirst(authorId, pageable)
                : tweetRepo.findUserTweetsAfter(authorId, cursor.getCreatedAt(), cursor.getId(), pageable);
        return toTweetPage(rows, size, userId);
    }

    @Transactional(readOnly = true)
    public TweetPageDto getUserReplies(String authorId, String userId, String cursorToken, int limit) {
        if (!userRepo.existsById(authorId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在");
        }
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Tweet> rows = cursor == null
                ? tweetRepo.findUserRepliesFirst(authorId, pageable)
                : tweetRepo.findUserRepliesAfter(authorId, cursor.getCreatedAt(), cursor.getId(), pageable);
        return toTweetPage(rows, size, userId);
    }

    @Transactional(readOnly = true)
    public TweetPageDto getBookmarks(String userId, String cursorToken, int limit) {
        return getInteractionPage(userId, InteractionType.BOOKMARK, cursorToken, limit);
    }

    @Transactional(readOnly = true)
    public TweetPageDto getLikes(String userId, String cursorToken, int limit) {
        return getInteractionPage(userId, InteractionType.LIKE, cursorToken, limit);
    }

    private TweetPageDto getInteractionPage(String userId, InteractionType type,
                                            String cursorToken, int limit) {
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        List<TweetInteractionRepo.InteractionRow> rows = cursor == null
                ? interactionRepo.findInteractionPageFirst(userId, type.getCode(), size + 1)
                : interactionRepo.findInteractionPageAfter(userId, type.getCode(),
                        cursor.getCreatedAt(), cursor.getId(), size + 1);

        boolean hasMore = rows.size() > size;
        if (hasMore) {
            rows = new ArrayList<>(rows.subList(0, size));
        }
        String nextCursor = null;
        if (hasMore && !rows.isEmpty()) {
            TweetInteractionRepo.InteractionRow last = rows.get(rows.size() - 1);
            nextCursor = new Cursor(last.getCreatedAt(), last.getTweetId()).encode();
        }

        List<Long> ids = rows.stream()
                .map(TweetInteractionRepo.InteractionRow::getTweetId)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return new TweetPageDto(Collections.emptyList(), nextCursor, hasMore);
        }

        Map<Long, Tweet> byId = tweetRepo.findActiveWithAuthorByIdIn(ids).stream()
                .collect(Collectors.toMap(Tweet::getId, t -> t));
        List<Tweet> ordered = new ArrayList<>();
        for (Long id : ids) {
            Tweet tweet = byId.get(id);
            if (tweet != null) {
                ordered.add(tweet);
            }
        }
        Map<Long, List<MediaDto>> media = loadMedia(ordered);
        Map<Long, Set<Integer>> flags = loadViewerFlags(userId, ordered);
        List<TweetDto> items = new ArrayList<>();
        for (Tweet tweet : ordered) {
            items.add(toDto(tweet, media.getOrDefault(tweet.getId(), Collections.emptyList()),
                    flags.getOrDefault(tweet.getId(), Collections.emptySet()), null, userId));
        }
        return new TweetPageDto(items, nextCursor, hasMore);
    }

    // -----------------------------------------------------------------------
    // Writes
    // -----------------------------------------------------------------------

    @Transactional
    public TweetDto create(String userId, String text, MultipartFile[] images, Long replyToId) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "请先登录"));
        String content = text == null ? "" : text.trim();
        if (content.isEmpty() && (images == null || images.length == 0)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_TWEET", "内容不能为空");
        }
        if (content.codePointCount(0, content.length()) > MAX_TEXT_LENGTH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "TEXT_TOO_LONG",
                    "最多输入 " + MAX_TEXT_LENGTH + " 个字符");
        }

        Tweet parent = null;
        if (replyToId != null) {
            parent = tweetRepo.findActiveWithAuthorById(replyToId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "TWEET_NOT_FOUND", "被回复的帖子不存在"));
        }

        List<StoredImage> stored = fileStorageService.store(images);
        if (content.isEmpty() && stored.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMPTY_TWEET", "内容不能为空");
        }

        try {
            Tweet tweet = new Tweet();
            tweet.setAuthor(author);
            tweet.setReplyToId(replyToId);
            tweet.setText(content);
            tweet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC).truncatedTo(java.time.temporal.ChronoUnit.MILLIS));
            Tweet saved = tweetRepo.saveAndFlush(tweet);

            List<TweetMedia> mediaRows = saveMedia(saved.getId(), stored);
            if (parent != null) {
                int changed = tweetRepo.changeReplyCount(parent.getId(), 1);
                if (changed == 0) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND", "被回复的帖子不存在");
                }
            }

            List<MediaDto> media = mediaRows.stream()
                    .map(this::toMediaDto)
                    .collect(Collectors.toList());
            return toDto(saved, media, Collections.emptySet(), null, userId);
        } catch (ApiException ex) {
            fileStorageService.delete(stored);
            throw ex;
        } catch (RuntimeException ex) {
            fileStorageService.delete(stored);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "CREATE_TWEET_FAILED",
                    "发布失败，请稍后重试");
        }
    }

    @Transactional
    public void delete(Long tweetId, String userId) {
        Tweet tweet = requireTweet(tweetId);
        if (!tweet.getAuthor().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "只能删除自己的帖子");
        }
        Long parentId = tweet.getReplyToId();
        int changed = tweetRepo.softDelete(tweetId, LocalDateTime.now(ZoneOffset.UTC));
        if (changed == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND", "帖子不存在");
        }
        if (parentId != null) {
            tweetRepo.changeReplyCount(parentId, -1);
        }
    }

    @Transactional
    public ActionStateDto setInteraction(Long tweetId, String userId,
                                         InteractionType type, boolean active) {
        Tweet tweet = requireTweet(tweetId);
        if (active) {
            int inserted = interactionRepo.insertIgnore(tweetId, userId, type.getCode(),
                    LocalDateTime.now(ZoneOffset.UTC));
            if (inserted == 1) {
                int changed = changeCounter(tweetId, type, 1);
                if (changed == 0) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "TWEET_NOT_FOUND", "帖子不存在");
                }
            }
        } else {
            int deleted = interactionRepo.deleteOne(tweetId, userId, (byte) type.getCode());
            if (deleted == 1) {
                changeCounter(tweetId, type, -1);
            }
        }

        Tweet fresh = requireTweet(tweetId);
        Set<Integer> flags = loadViewerFlags(userId, Collections.singletonList(fresh))
                .getOrDefault(fresh.getId(), Collections.emptySet());
        ViewerDto viewer = new ViewerDto(flags.contains(InteractionType.LIKE.getCode()),
                flags.contains(InteractionType.REPOST.getCode()),
                flags.contains(InteractionType.BOOKMARK.getCode()));
        return new ActionStateDto(String.valueOf(fresh.getId()), toStats(fresh), viewer);
    }

    public MeDto getMe(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在"));
        return new MeDto(user.getId(), user.getName(), user.getProfilePhoto());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private TweetPageDto toTweetPage(List<Tweet> rows, int limit, String userId) {
        boolean hasMore = rows.size() > limit;
        if (hasMore) {
            rows = new ArrayList<>(rows.subList(0, limit));
        }
        String nextCursor = null;
        if (hasMore && !rows.isEmpty()) {
            Tweet last = rows.get(rows.size() - 1);
            nextCursor = new Cursor(last.getCreatedAt(), last.getId()).encode();
        }

        Map<Long, List<MediaDto>> media = loadMedia(rows);
        Map<Long, Set<Integer>> flags = loadViewerFlags(userId, rows);
        List<TweetDto> items = new ArrayList<>();
        for (Tweet tweet : rows) {
            items.add(toDto(tweet, media.getOrDefault(tweet.getId(), Collections.emptyList()),
                    flags.getOrDefault(tweet.getId(), Collections.emptySet()), null, userId));
        }
        return new TweetPageDto(items, nextCursor, hasMore);
    }

    private Map<Long, List<MediaDto>> loadMedia(Collection<Tweet> tweets) {
        Map<Long, List<MediaDto>> result = new HashMap<>();
        if (tweets == null || tweets.isEmpty()) {
            return result;
        }
        List<Long> ids = tweets.stream().map(Tweet::getId).collect(Collectors.toList());
        List<TweetMedia> rows = mediaRepo.findForTweets(ids);
        for (TweetMedia row : rows) {
            result.computeIfAbsent(row.getId().getTweetId(), key -> new ArrayList<>())
                    .add(toMediaDto(row));
        }
        return result;
    }

    private Map<Long, Set<Integer>> loadViewerFlags(String userId, Collection<Tweet> tweets) {
        Map<Long, Set<Integer>> result = new HashMap<>();
        if (userId == null || tweets == null || tweets.isEmpty()) {
            return result;
        }
        List<Long> ids = tweets.stream().map(Tweet::getId).collect(Collectors.toList());
        List<TweetInteraction> rows = interactionRepo.findByUserIdAndTweetIds(userId, ids);
        for (TweetInteraction row : rows) {
            result.computeIfAbsent(row.getId().getTweetId(), key -> new HashSet<>())
                    .add(row.getId().getType().intValue());
        }
        return result;
    }

    private TweetDto toDto(Tweet tweet, List<MediaDto> media, Set<Integer> flags,
                           CompactTweetDto replyTo, String currentUserId) {
        ViewerDto viewer = currentUserId == null ? null : new ViewerDto(
                flags.contains(InteractionType.LIKE.getCode()),
                flags.contains(InteractionType.REPOST.getCode()),
                flags.contains(InteractionType.BOOKMARK.getCode()));
        return new TweetDto(
                String.valueOf(tweet.getId()),
                toAuthor(tweet.getAuthor()),
                tweet.getText(),
                media,
                toInstant(tweet.getCreatedAt()),
                toInstant(tweet.getEditedAt()),
                tweet.getReplyToId() == null ? null : String.valueOf(tweet.getReplyToId()),
                replyTo,
                toStats(tweet),
                viewer);
    }

    private CompactTweetDto toCompact(Tweet tweet, List<MediaDto> media) {
        return new CompactTweetDto(String.valueOf(tweet.getId()), toAuthor(tweet.getAuthor()),
                tweet.getText(), media);
    }

    private AuthorDto toAuthor(User user) {
        return user == null ? null : new AuthorDto(user.getId(), user.getName(), user.getProfilePhoto());
    }

    private MediaDto toMediaDto(TweetMedia media) {
        return new MediaDto(media.getUrl(), media.getWidth(), media.getHeight(), media.getAltText());
    }

    private StatsDto toStats(Tweet tweet) {
        return new StatsDto(tweet.getReplyCount(), tweet.getRepostCount(),
                tweet.getLikeCount(), tweet.getBookmarkCount(), tweet.getViewCount());
    }

    private Instant toInstant(LocalDateTime time) {
        return time == null ? null : time.toInstant(ZoneOffset.UTC);
    }

    private List<TweetMedia> saveMedia(Long tweetId, List<StoredImage> images) {
        List<TweetMedia> rows = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            StoredImage image = images.get(i);
            TweetMedia media = new TweetMedia();
            media.setId(new TweetMediaId(tweetId, (byte) i));
            media.setUrl(image.getPath());
            media.setWidth(image.getWidth());
            media.setHeight(image.getHeight());
            media.setMimeType(image.getMimeType());
            media.setSizeBytes(image.getSizeBytes());
            rows.add(media);
        }
        if (!rows.isEmpty()) {
            mediaRepo.saveAll(rows);
        }
        return rows;
    }

    private int changeCounter(Long tweetId, InteractionType type, int delta) {
        switch (type) {
            case LIKE:
                return tweetRepo.changeLikeCount(tweetId, delta);
            case REPOST:
                return tweetRepo.changeRepostCount(tweetId, delta);
            case BOOKMARK:
                return tweetRepo.changeBookmarkCount(tweetId, delta);
            default:
                return 0;
        }
    }

    private Tweet requireTweet(Long tweetId) {
        return tweetRepo.findActiveWithAuthorById(tweetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "TWEET_NOT_FOUND", "帖子不存在"));
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(limit, MAX_PAGE_SIZE);
    }

    private Cursor parseCursor(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            return Cursor.decode(token);
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_CURSOR", "游标无效");
        }
    }
}
