package com.twitter.service;

import com.twitter.dto.FollowPageDto;
import com.twitter.dto.FollowStateDto;
import com.twitter.dto.FollowUserDto;
import com.twitter.entity.Follow;
import com.twitter.entity.User;
import com.twitter.exception.ApiException;
import com.twitter.repo.FollowRepo;
import com.twitter.repo.UserRepo;
import com.twitter.util.Cursor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
public class FollowService {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final FollowRepo followRepo;
    private final UserRepo userRepo;

    public FollowService(FollowRepo followRepo, UserRepo userRepo) {
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<String> followingIds(String userId) {
        return followRepo.findFolloweeIdsByFollowerId(userId);
    }

    @Transactional(readOnly = true)
    public FollowPageDto followers(String userId, String viewerId, String cursorToken, int limit) {
        return page(false, userId, viewerId, cursorToken, limit);
    }

    @Transactional(readOnly = true)
    public FollowPageDto following(String userId, String viewerId, String cursorToken, int limit) {
        return page(true, userId, viewerId, cursorToken, limit);
    }

    @Transactional(readOnly = true)
    public List<FollowUserDto> suggestions(String viewerId, int limit) {
        int size = limit <= 0 ? 5 : Math.min(limit, MAX_PAGE_SIZE);
        List<User> users = userRepo.findSuggestions(viewerId, PageRequest.of(0, size));
        List<String> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        Set<String> followsViewer = viewerFollowersAmong(viewerId, userIds);
        List<FollowUserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(new FollowUserDto(user.getId(), user.getName(), user.getProfilePhoto(),
                    false, followsViewer.contains(user.getId()), false));
        }
        return result;
    }

    @Transactional
    public FollowStateDto setFollowing(String viewerId, String targetId, boolean active) {
        String target = normalizeTarget(targetId);
        if (target.equals(viewerId)) {
            if (active) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "FOLLOW_SELF", "不能关注自己");
            }
            return new FollowStateDto(target, false,
                    followRepo.countByFolloweeId(target), false);
        }
        requireUser(target);

        if (active) {
            followRepo.insertIgnore(viewerId, target,
                    LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS));
        } else {
            followRepo.deleteOne(viewerId, target);
        }
        boolean followsYou = followRepo.existsByFollowerIdAndFolloweeId(target, viewerId);
        return new FollowStateDto(target, active,
                followRepo.countByFolloweeId(target), followsYou);
    }

    private FollowPageDto page(boolean followingDirection, String userId, String viewerId,
                               String cursorToken, int limit) {
        requireUser(userId);
        int size = normalizeLimit(limit);
        Cursor cursor = parseCursor(cursorToken);
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Follow> rows;
        if (followingDirection) {
            rows = cursor == null
                    ? followRepo.findFollowingFirst(userId, pageable)
                    : followRepo.findFollowingAfter(userId, cursor.getCreatedAt(),
                            cursor.getId(), pageable);
        } else {
            rows = cursor == null
                    ? followRepo.findFollowersFirst(userId, pageable)
                    : followRepo.findFollowersAfter(userId, cursor.getCreatedAt(),
                            cursor.getId(), pageable);
        }

        boolean hasMore = rows.size() > size;
        if (hasMore) {
            rows = new ArrayList<>(rows.subList(0, size));
        }
        String nextCursor = null;
        if (hasMore && !rows.isEmpty()) {
            Follow last = rows.get(rows.size() - 1);
            nextCursor = new Cursor(last.getCreatedAt(), last.getId()).encode();
        }

        List<String> userIds = rows.stream()
                .map(followingDirection ? Follow::getFolloweeId : Follow::getFollowerId)
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return new FollowPageDto(Collections.emptyList(), nextCursor, hasMore);
        }

        Map<String, User> users = new HashMap<>();
        for (User user : userRepo.findAllById(userIds)) {
            users.put(user.getId(), user);
        }
        Set<String> viewerFollowing = viewerFollowsAmong(viewerId, userIds);
        Set<String> viewerFollowers = viewerFollowersAmong(viewerId, userIds);

        List<FollowUserDto> items = new ArrayList<>();
        for (String id : userIds) {
            User user = users.get(id);
            if (user == null) {
                continue;
            }
            items.add(new FollowUserDto(user.getId(), user.getName(),
                    user.getProfilePhoto(), viewerFollowing.contains(id),
                    viewerFollowers.contains(id), id.equals(viewerId)));
        }
        return new FollowPageDto(items, nextCursor, hasMore);
    }

    private Set<String> viewerFollowsAmong(String viewerId, Collection<String> userIds) {
        if (viewerId == null || userIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(followRepo.findFolloweeIdsAmong(viewerId, userIds));
    }

    private Set<String> viewerFollowersAmong(String viewerId, Collection<String> userIds) {
        if (viewerId == null || userIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(followRepo.findFollowerIdsAmong(viewerId, userIds));
    }

    private User requireUser(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND", "用户不存在"));
    }

    private String normalizeTarget(String targetId) {
        if (targetId == null || targetId.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TARGET", "关注目标不能为空");
        }
        return targetId.trim();
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
