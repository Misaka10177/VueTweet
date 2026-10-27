-- JWT access token + rotating refresh token authentication.
-- Old opaque UUID tokens are discarded together with user_auth.

CREATE TABLE `user_credential` (
  `user_id`             varchar(50)  NOT NULL,
  `password_hash`       varchar(100) NOT NULL,
  `password_updated_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `failed_attempts`     int(11)      NOT NULL DEFAULT 0,
  `locked_until`        datetime(6)  DEFAULT NULL,
  `last_login_at`       datetime(6)  DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_credential_user`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `auth_session` (
  `id`            char(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `user_id`       varchar(50) NOT NULL,
  `device_name`   varchar(100) DEFAULT NULL,
  `user_agent`    varchar(512) DEFAULT NULL,
  `ip_address`    varchar(45)  DEFAULT NULL,
  `created_at`    datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `last_used_at`  datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `expires_at`    datetime(6)  NOT NULL,
  `revoked_at`    datetime(6)  DEFAULT NULL,
  `revoke_reason` varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_auth_session_user` (`user_id`, `revoked_at`),
  KEY `idx_auth_session_expires` (`expires_at`),
  CONSTRAINT `fk_auth_session_user`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `auth_refresh_token` (
  `id`             bigint(20) NOT NULL AUTO_INCREMENT,
  `session_id`     char(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `token_hash`     char(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `issued_at`      datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `expires_at`     datetime(6) NOT NULL,
  `used_at`        datetime(6) DEFAULT NULL,
  `revoked_at`     datetime(6) DEFAULT NULL,
  `replaced_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_auth_refresh_token_hash` (`token_hash`),
  KEY `idx_auth_refresh_session` (`session_id`, `expires_at`),
  KEY `idx_auth_refresh_replaced_by` (`replaced_by_id`),
  CONSTRAINT `fk_auth_refresh_session`
    FOREIGN KEY (`session_id`) REFERENCES `auth_session` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_auth_refresh_replaced_by`
    FOREIGN KEY (`replaced_by_id`) REFERENCES `auth_refresh_token` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `auth_audit_log` (
  `id`         bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id`    varchar(50) DEFAULT NULL,
  `event_type` varchar(30) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` varchar(512) DEFAULT NULL,
  `detail`     varchar(500) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_auth_audit_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- The legacy table stored plaintext opaque tokens and SHA-256 passwords. It is
-- intentionally not migrated: credentials are recreated with BCrypt below.
DROP TABLE `user_auth`;
