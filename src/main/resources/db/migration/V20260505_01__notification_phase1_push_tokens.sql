CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS user_push_tokens (
  id UUID PRIMARY KEY,
  user_id VARCHAR(128) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  platform VARCHAR(10) NOT NULL,
  provider VARCHAR(10) NOT NULL,
  token TEXT NOT NULL,
  device_id VARCHAR(128),
  app_version VARCHAR(20),
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  last_used_at TIMESTAMP,
  failure_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT uk_user_push_tokens_user_token UNIQUE(user_id, token)
);

CREATE INDEX IF NOT EXISTS idx_push_tokens_user_enabled
  ON user_push_tokens(user_id, enabled);

INSERT INTO user_push_tokens (
  id,
  user_id,
  platform,
  provider,
  token,
  enabled,
  last_used_at,
  failure_count,
  created_at,
  updated_at
)
SELECT
  gen_random_uuid(),
  user_id,
  'ios',
  'apns',
  fcm_token,
  TRUE,
  CURRENT_TIMESTAMP,
  0,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM user_settings
WHERE fcm_token IS NOT NULL
  AND btrim(fcm_token) <> ''
ON CONFLICT (user_id, token) DO NOTHING;

ALTER TABLE user_settings
  ADD COLUMN IF NOT EXISTS notify_like BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS notify_comment BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS notify_follow BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS notify_achievement BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS notify_system BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS notify_marketing BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS marketing_agreed_at TIMESTAMP,
  ADD COLUMN IF NOT EXISTS quiet_hours_start TIME,
  ADD COLUMN IF NOT EXISTS quiet_hours_end TIME;

COMMENT ON COLUMN user_settings.fcm_token IS 'Deprecated. Migrated to user_push_tokens; kept for backward compatibility.';
