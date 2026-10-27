package com.twitter.repo;

import com.twitter.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuthSessionRepo extends JpaRepository<AuthSession, String> {

    List<AuthSession> findByUserIdAndRevokedAtIsNullAndExpiresAtAfterOrderByLastUsedAtDesc(
            String userId, LocalDateTime now);

    List<AuthSession> findByUserIdAndRevokedAtIsNullAndExpiresAtAfterOrderByLastUsedAtAsc(
            String userId, LocalDateTime now);

    long countByUserIdAndRevokedAtIsNullAndExpiresAtAfter(String userId, LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AuthSession s where s.id = :id")
    Optional<AuthSession> findByIdForUpdate(@Param("id") String id);
}
