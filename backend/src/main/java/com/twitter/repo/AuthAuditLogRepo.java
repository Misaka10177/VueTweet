package com.twitter.repo;

import com.twitter.entity.AuthAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAuditLogRepo extends JpaRepository<AuthAuditLog, Long> {
}
