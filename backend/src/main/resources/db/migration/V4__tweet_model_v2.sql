-- Tweet / interaction model v2.
-- MySQL 5.7, existing data is preserved.
--
-- Conventions:
--   * timestamps are stored in UTC (the old rows were written with the JVM's
--     Asia/Shanghai wall clock, so they are shifted once here);
--   * tweet counters become bigint caches maintained by the service;
--   * images move from tweet.images JSON to tweet_media rows;
--   * tweet_interaction gets a composite primary key and numeric type.

-- ---------------------------------------------------------------------------
-- 1. Timestamps: old LocalDateTime values were local wall time (+08:00).
-- ---------------------------------------------------------------------------
UPDATE tweet SET created_at = DATE_SUB(created_at, INTERVAL 8 HOUR);
UPDATE tweet_interaction SET created_at = DATE_SUB(created_at, INTERVAL 8 HOUR);
UPDATE follow SET created_at = DATE_SUB(created_at, INTERVAL 8 HOUR);

-- ---------------------------------------------------------------------------
-- 2. Tweet columns.
-- ---------------------------------------------------------------------------
UPDATE tweet SET text = '' WHERE text IS NULL;

ALTER TABLE tweet
  MODIFY text varchar(1000) NOT NULL DEFAULT '',
  MODIFY created_at datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  MODIFY view_count bigint(20) NOT NULL DEFAULT 0,
  ADD COLUMN deleted_at datetime(3) NULL AFTER created_at,
  ADD COLUMN edited_at datetime(3) NULL AFTER deleted_at,
  ADD COLUMN reply_count bigint(20) NOT NULL DEFAULT 0 AFTER edited_at,
  ADD COLUMN repost_count bigint(20) NOT NULL DEFAULT 0 AFTER reply_count,
  ADD COLUMN like_count bigint(20) NOT NULL DEFAULT 0 AFTER repost_count,
  ADD COLUMN bookmark_count bigint(20) NOT NULL DEFAULT 0 AFTER like_count;

-- ---------------------------------------------------------------------------
-- 3. Backfill counters from the interaction / reply facts.
-- ---------------------------------------------------------------------------
UPDATE tweet t
LEFT JOIN (
  SELECT tweet_id,
         SUM(type = 'upvote')    AS likes,
         SUM(type = 'transpond') AS reposts,
         SUM(type = 'bookmark')  AS bookmarks
  FROM tweet_interaction
  GROUP BY tweet_id
) x ON x.tweet_id = t.id
SET t.like_count     = COALESCE(x.likes, 0),
    t.repost_count   = COALESCE(x.reposts, 0),
    t.bookmark_count = COALESCE(x.bookmarks, 0);

UPDATE tweet t
LEFT JOIN (
  SELECT reply_to_id, COUNT(*) AS c
  FROM tweet
  WHERE reply_to_id IS NOT NULL
  GROUP BY reply_to_id
) x ON x.reply_to_id = t.id
SET t.reply_count = COALESCE(x.c, 0);

-- ---------------------------------------------------------------------------
-- 4. Media table + legacy images backfill.
-- ---------------------------------------------------------------------------
CREATE TABLE `tweet_media` (
  `tweet_id`   bigint(20) NOT NULL,
  `sort_order` tinyint(3) unsigned NOT NULL,
  `url`        varchar(512) NOT NULL,
  `width`      int(10) unsigned DEFAULT NULL,
  `height`     int(10) unsigned DEFAULT NULL,
  `mime_type`  varchar(64) DEFAULT NULL,
  `size_bytes` int(10) unsigned DEFAULT NULL,
  `alt_text`   varchar(420) DEFAULT NULL,
  PRIMARY KEY (`tweet_id`, `sort_order`),
  CONSTRAINT `fk_media_tweet`
    FOREIGN KEY (`tweet_id`) REFERENCES `tweet` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

INSERT INTO tweet_media (tweet_id, sort_order, url)
SELECT t.id, n.n,
       JSON_UNQUOTE(JSON_EXTRACT(t.images, CONCAT('$[', n.n, ']')))
FROM tweet t
JOIN (
  SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3
  UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
  UNION ALL SELECT 7 UNION ALL SELECT 8
) n
WHERE t.images IS NOT NULL
  AND JSON_VALID(t.images)
  AND n.n < JSON_LENGTH(t.images)
  AND JSON_EXTRACT(t.images, CONCAT('$[', n.n, ']')) IS NOT NULL;

-- ---------------------------------------------------------------------------
-- 5. Interaction table: composite PK + tinyint type.
-- ---------------------------------------------------------------------------
CREATE TABLE `tweet_interaction_v2` (
  `tweet_id`   bigint(20) NOT NULL,
  `user_id`    varchar(50) NOT NULL,
  `type`       tinyint(3) unsigned NOT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`tweet_id`, `user_id`, `type`),
  KEY `idx_interaction_user` (`user_id`, `type`, `created_at`),
  KEY `idx_interaction_tweet` (`tweet_id`, `type`, `created_at`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

INSERT INTO tweet_interaction_v2 (tweet_id, user_id, type, created_at)
SELECT tweet_id, user_id,
       CASE type
         WHEN 'upvote' THEN 1
         WHEN 'transpond' THEN 2
         WHEN 'bookmark' THEN 3
       END,
       created_at
FROM tweet_interaction
WHERE type IN ('upvote', 'transpond', 'bookmark');

DROP TABLE tweet_interaction;
RENAME TABLE tweet_interaction_v2 TO tweet_interaction;

ALTER TABLE tweet_interaction
  ADD CONSTRAINT `fk_interaction_tweet`
    FOREIGN KEY (`tweet_id`) REFERENCES `tweet` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_interaction_user`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

-- ---------------------------------------------------------------------------
-- 6. Tweet foreign key + indexes, then drop legacy columns.
-- ---------------------------------------------------------------------------
ALTER TABLE tweet DROP FOREIGN KEY fk_tweet_reply;

ALTER TABLE tweet ADD INDEX idx_tweet_user (user_id, deleted_at, created_at, id);
ALTER TABLE tweet DROP INDEX idx_tweet_feed;
ALTER TABLE tweet ADD INDEX idx_tweet_feed (reply_to_id, deleted_at, created_at, id);
ALTER TABLE tweet DROP INDEX idx_tweet_user_created;

ALTER TABLE tweet
  ADD CONSTRAINT `fk_tweet_reply`
    FOREIGN KEY (`reply_to_id`) REFERENCES `tweet` (`id`) ON DELETE CASCADE;

ALTER TABLE tweet
  DROP COLUMN reply,
  DROP COLUMN transpond,
  DROP COLUMN upvote,
  DROP COLUMN bookmark,
  DROP COLUMN images;
