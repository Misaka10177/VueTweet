package com.twitter.service;

import com.twitter.dto.UserProfileDto;
import com.twitter.dto.UserSummaryDto;
import com.twitter.entity.User;
import com.twitter.exception.ApiException;
import com.twitter.repo.FollowRepo;
import com.twitter.repo.TweetRepo;
import com.twitter.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final FollowRepo followRepo;
    private final TweetRepo tweetRepo;

    public UserService(UserRepo userRepo, FollowRepo followRepo, TweetRepo tweetRepo) {
        this.userRepo = userRepo;
        this.followRepo = followRepo;
        this.tweetRepo = tweetRepo;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> listUsers() {
        List<UserSummaryDto> result = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            result.add(new UserSummaryDto(user.getId(), user.getName(), user.getProfilePhoto()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String targetId, String viewerId) {
        User user = userRepo.findById(targetId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND", "用户不存在"));
        boolean self = user.getId().equals(viewerId);
        boolean viewerFollowing = !self && viewerId != null
                && followRepo.existsByFollowerIdAndFolloweeId(viewerId, user.getId());
        return new UserProfileDto(user.getId(), user.getName(), user.getProfilePhoto(),
                followRepo.countByFollowerId(user.getId()),
                followRepo.countByFolloweeId(user.getId()),
                tweetRepo.countByAuthorIdAndReplyToIdIsNullAndDeletedAtIsNull(user.getId()),
                viewerFollowing, self);
    }

    @Transactional(readOnly = true)
    public boolean exists(String userId) {
        return userRepo.existsById(userId);
    }
}
