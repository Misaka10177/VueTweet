-- Follow lists are read in creation order for both directions.
-- The old single-column followee index is a prefix of the new composite one.

ALTER TABLE follow
  MODIFY created_at datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3);

ALTER TABLE follow
  DROP INDEX idx_follow_followee,
  ADD INDEX idx_follow_follower_created (follower_id, created_at, id),
  ADD INDEX idx_follow_followee_created (followee_id, created_at, id);
