-- Baseline schema before the 2026-10 optimization.
-- Existing databases are baselined at version 1, so this file is only executed on a fresh database.
-- V2 then normalizes and constrains the schema.

CREATE TABLE `user` (
  `name` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_auth` (
  `user_id` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `access_token` varchar(255) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `token_created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tweet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reply` int(11) NOT NULL,
  `text` text,
  `transpond` int(11) NOT NULL,
  `upvote` int(11) NOT NULL,
  `view_count` int(11) DEFAULT NULL,
  `images` text,
  `bookmark` int(11) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tweet_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tweet_id` bigint(20) NOT NULL,
  `reply_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tweet_reply` (`tweet_id`, `reply_id`),
  UNIQUE KEY `UKjaxa5dv69wbdyi4ibyy9f9yut` (`tweet_id`, `reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tweet_interaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tweet_id` bigint(20) NOT NULL,
  `user_id` varchar(100) NOT NULL,
  `type` varchar(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tweet_user_type` (`tweet_id`, `user_id`, `type`),
  UNIQUE KEY `UK13o1910lt2js8qcsycboi9w2l` (`tweet_id`, `user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `trend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `topic` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `following` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
