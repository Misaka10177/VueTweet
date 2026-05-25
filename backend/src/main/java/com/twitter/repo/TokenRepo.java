package com.twitter.repo;

import com.twitter.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByUserId(String userId);
    Token findByAccessToken(String accessToken);
    Token findByRefreshToken(String refreshToken);
}
