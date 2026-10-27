-- Normalize relationships, add foreign keys, indexes and remove redundant structures.

-- Users: make the primary key explicit and align auth FK types.
ALTER TABLE `user` MODIFY `id` varchar(50) NOT NULL;
ALTER TABLE `user` ADD PRIMARY KEY (`id`);

ALTER TABLE `user_auth` ENGINE=InnoDB;
ALTER TABLE `user_auth` MODIFY `user_id` varchar(50) NOT NULL;
ALTER TABLE `user_auth` MODIFY `access_token` varchar(100) DEFAULT NULL;
ALTER TABLE `user_auth` MODIFY `refresh_token` varchar(100) DEFAULT NULL;
ALTER TABLE `user_auth` ADD UNIQUE KEY `uk_user_auth_access` (`access_token`);
ALTER TABLE `user_auth` ADD UNIQUE KEY `uk_user_auth_refresh` (`refresh_token`);
ALTER TABLE `user_auth`
  ADD CONSTRAINT `fk_user_auth_user`
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

-- Tweets: reply is a self-reference, not a separate relation table.
ALTER TABLE `tweet` MODIFY `user_id` varchar(50) NOT NULL;
ALTER TABLE `tweet` MODIFY `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);
ALTER TABLE `tweet` MODIFY `view_count` int(11) NOT NULL DEFAULT 0;
ALTER TABLE `tweet` MODIFY `reply` int(11) NOT NULL DEFAULT 0;
ALTER TABLE `tweet` MODIFY `transpond` int(11) NOT NULL DEFAULT 0;
ALTER TABLE `tweet` MODIFY `upvote` int(11) NOT NULL DEFAULT 0;
ALTER TABLE `tweet` MODIFY `bookmark` int(11) NOT NULL DEFAULT 0;
ALTER TABLE `tweet` ADD COLUMN `reply_to_id` bigint(20) DEFAULT NULL AFTER `user_id`;

UPDATE `tweet` t
JOIN `tweet_reply` tr ON tr.`reply_id` = t.`id`
SET t.`reply_to_id` = tr.`tweet_id`;

ALTER TABLE `tweet` ADD INDEX `idx_tweet_feed` (`reply_to_id`, `created_at`, `id`);
ALTER TABLE `tweet` ADD INDEX `idx_tweet_user_created` (`user_id`, `created_at`);
ALTER TABLE `tweet`
  ADD CONSTRAINT `fk_tweet_user`
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;
ALTER TABLE `tweet`
  ADD CONSTRAINT `fk_tweet_reply`
  FOREIGN KEY (`reply_to_id`) REFERENCES `tweet` (`id`) ON DELETE SET NULL;

-- Interactions: transaction-safe InnoDB table with a single unique constraint.
ALTER TABLE `tweet_interaction` ENGINE=InnoDB;
ALTER TABLE `tweet_interaction` MODIFY `user_id` varchar(50) NOT NULL;
ALTER TABLE `tweet_interaction` MODIFY `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);
ALTER TABLE `tweet_interaction` DROP INDEX `UK13o1910lt2js8qcsycboi9w2l`;
ALTER TABLE `tweet_interaction` ADD INDEX `idx_interaction_user_type` (`user_id`, `type`, `created_at`);
ALTER TABLE `tweet_interaction` ADD INDEX `idx_interaction_tweet_type` (`tweet_id`, `type`);
ALTER TABLE `tweet_interaction`
  ADD CONSTRAINT `fk_interaction_tweet`
  FOREIGN KEY (`tweet_id`) REFERENCES `tweet` (`id`) ON DELETE CASCADE;
ALTER TABLE `tweet_interaction`
  ADD CONSTRAINT `fk_interaction_user`
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

-- Remove the old reply relation table and the unused in-memory following table.
DROP TABLE `tweet_reply`;
DROP TABLE `following`;

CREATE TABLE `follow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `follower_id` varchar(50) NOT NULL,
  `followee_id` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow_pair` (`follower_id`, `followee_id`),
  KEY `idx_follow_followee` (`followee_id`),
  CONSTRAINT `fk_follow_follower` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_follow_followee` FOREIGN KEY (`followee_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Trend category used to contain the suffix; the API/template should add it.
ALTER TABLE `trend` ENGINE=InnoDB;
UPDATE `trend`
SET `category` = TRIM(REPLACE(`category`, ' 的趋势', ''))
WHERE `category` LIKE '% 的趋势';

ALTER TABLE `trend` MODIFY `category` varchar(80) NOT NULL;
ALTER TABLE `trend` MODIFY `topic` varchar(160) NOT NULL;
ALTER TABLE `trend` ADD UNIQUE KEY `uk_trend_category_topic` (`category`, `topic`);
