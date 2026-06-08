package com.twitter.repo;

import com.twitter.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepo extends JpaRepository<UserAuth, String> {
    UserAuth findByAccessToken(String accessToken);
    UserAuth findByRefreshToken(String refreshToken);
}
