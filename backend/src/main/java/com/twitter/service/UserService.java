package com.twitter.service;

import com.twitter.dto.CurrentUserDto;
import com.twitter.dto.UserSummaryDto;
import com.twitter.entity.User;
import com.twitter.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
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
    public CurrentUserDto currentUser(String userId) {
        Optional<User> user = userRepo.findById(userId);
        return user.map(value -> new CurrentUserDto(value.getId(), value.getName())).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean exists(String userId) {
        return userRepo.existsById(userId);
    }
}
