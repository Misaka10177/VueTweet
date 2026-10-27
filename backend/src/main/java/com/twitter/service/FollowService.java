package com.twitter.service;

import com.twitter.entity.Follow;
import com.twitter.exception.ApiException;
import com.twitter.repo.FollowRepo;
import com.twitter.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FollowService {
    private final FollowRepo followRepo;
    private final UserRepo userRepo;

    public FollowService(FollowRepo followRepo, UserRepo userRepo) {
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<String> following(String userId) {
        List<String> result = new ArrayList<>();
        for (Follow follow : followRepo.findByFollowerIdOrderByCreatedAtDesc(userId)) {
            result.add(follow.getFolloweeId());
        }
        return result;
    }

    @Transactional
    public void follow(String userId, String targetId) {
        if (targetId == null || targetId.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TARGET", "关注目标不能为空");
        }
        String target = targetId.trim();
        if (target.equals(userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "FOLLOW_SELF", "不能关注自己");
        }
        if (!userRepo.existsById(target)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "用户不存在");
        }
        if (followRepo.findByFollowerIdAndFolloweeId(userId, target).isPresent()) {
            return;
        }
        Follow follow = new Follow();
        follow.setFollowerId(userId);
        follow.setFolloweeId(target);
        follow.setCreatedAt(LocalDateTime.now());
        followRepo.save(follow);
    }

    @Transactional
    public void unfollow(String userId, String targetId) {
        followRepo.findByFollowerIdAndFolloweeId(userId, targetId)
                .ifPresent(followRepo::delete);
    }
}
