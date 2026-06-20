-- ============================================================
-- WATCH_MIGRATION.sql  (EC2에서 수동 실행 → 그다음 JAR 배포)
-- 관련 기획: swwim/WATCH_INTEGRATION_PLAN.md , 함정: swwim/WATCH_INTEGRATION_PITFALLS.md
-- 규칙: ddl-auto=validate 이므로 "SQL 먼저, JAR 나중" (CLAUDE.md / INV-6)
-- 모든 문장은 멱등(IF EXISTS / IF NOT EXISTS)하게 작성 — 재실행해도 안전.
-- ⚠️ postgresql_schema.sql 은 STALE. 운영 DB는 JPA 엔티티 기준이다(이미 session_id 등 존재).
-- ============================================================


-- ============================================================
-- [Phase 1-A] 저장 흐름 단일화 — started_at/ended_at + sessionId 멱등
-- ============================================================

-- ── 0) 사전 점검: calendar_events 에 UNIQUE(user_id, date)가 살아있는지 확인 ──
--    살아있으면 "하루 N회 훈련 = N행"(INV-2)이 불가능하므로 반드시 제거해야 한다.
--    먼저 아래로 확인:
--    SELECT conname FROM pg_constraint
--      WHERE conrelid = 'calendar_events'::regclass AND contype = 'u';
--    결과에 (user_id,date) 유니크가 있으면 아래 DROP 실행(이름이 다르면 그 이름으로).
ALTER TABLE calendar_events DROP CONSTRAINT IF EXISTS calendar_events_user_id_date_key;
DO $$
DECLARE c text;
BEGIN
  SELECT con.conname INTO c
  FROM pg_constraint con
  JOIN pg_attribute a ON a.attrelid = con.conrelid AND a.attnum = ANY(con.conkey)
  WHERE con.conrelid = 'calendar_events'::regclass AND con.contype = 'u'
  GROUP BY con.conname
  HAVING array_agg(a.attname ORDER BY a.attname) = ARRAY['date','user_id'];
  IF c IS NOT NULL THEN
    EXECUTE format('ALTER TABLE calendar_events DROP CONSTRAINT %I', c);
    RAISE NOTICE 'dropped stale unique constraint: %', c;
  END IF;
END $$;

-- ── 1) started_at / ended_at 컬럼 추가 (TIMESTAMPTZ = 절대시각, 매칭 기준) ──
--    엔티티 필드 타입은 java.time.Instant (또는 OffsetDateTime) 권장 → TIMESTAMPTZ 매핑.
ALTER TABLE training_sessions ADD COLUMN IF NOT EXISTS started_at TIMESTAMPTZ;  -- 캐노니컬(세션) 시작
ALTER TABLE training_sessions ADD COLUMN IF NOT EXISTS ended_at   TIMESTAMPTZ;  -- 캐노니컬(세션) 종료
ALTER TABLE calendar_events   ADD COLUMN IF NOT EXISTS started_at TIMESTAMPTZ;  -- 비정규화(매칭/표시용)
ALTER TABLE calendar_events   ADD COLUMN IF NOT EXISTS ended_at   TIMESTAMPTZ;

-- ── 2) sessionId 기준 멱등 보장 (INV-2) ──
--    같은 세션 = 1행. session_id 가 있는 행만 유일. 계획 이벤트(session_id NULL)는 제외.
--    이 부분 유니크 인덱스가 곧 dedup 키이자 upsert 안전장치.
CREATE UNIQUE INDEX IF NOT EXISTS ux_calendar_events_user_session
  ON calendar_events (user_id, session_id)
  WHERE session_id IS NOT NULL;

-- (조회 성능) 세션으로 캘린더행 찾기
CREATE INDEX IF NOT EXISTS idx_calendar_events_session
  ON calendar_events (session_id);

-- ── 3) (선택) 기존 0초 쓰레기 이벤트 정리 ──
--    P-1 로 인해 쌓였을 수 있는 "완료인데 total_time='00:00:00' & total_distance=0" 자동행.
--    실데이터 수동행과 같은 session_id로 중복돼 있을 가능성 → 운영자 확인 후 수동 실행 권장.
--    DRY-RUN: 먼저 개수만 확인
--    SELECT count(*) FROM calendar_events
--      WHERE completed = TRUE AND (total_time = '00:00:00' OR total_time IS NULL)
--        AND COALESCE(total_distance,0) = 0;
--    실제 삭제는 검토 후:
--    DELETE FROM calendar_events
--      WHERE completed = TRUE AND (total_time = '00:00:00' OR total_time IS NULL)
--        AND COALESCE(total_distance,0) = 0;


-- ============================================================
-- [Phase 1-B] watch_workouts (참고 — 1-B 착수 시 실행, 지금은 미실행)
-- ============================================================
-- CREATE TABLE IF NOT EXISTS watch_workouts (
--   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--   user_id VARCHAR(128) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
--   calendar_event_id UUID REFERENCES calendar_events(id) ON DELETE SET NULL,  -- INV-3
--   source VARCHAR(20) NOT NULL,            -- APPLE_HEALTH | HEALTH_CONNECT
--   external_id VARCHAR(255) NOT NULL,      -- HealthKit UUID / Health Connect recordId
--   workout_type VARCHAR(30),               -- SWIMMING_POOL | SWIMMING_OPEN_WATER | SWIMMING_UNKNOWN | UNKNOWN
--   started_at TIMESTAMPTZ, ended_at TIMESTAMPTZ, total_duration INT,
--   total_distance INT, avg_pace_sec_per_100m DOUBLE PRECISION, avg_speed DOUBLE PRECISION,
--   active_calories INT, avg_heart_rate INT, max_heart_rate INT, stroke_count INT,
--   swolf INT, stroke_style VARCHAR(30),
--   heart_rate_samples JSONB, laps JSONB, gps_route JSONB, raw JSONB,
--   match_confidence DOUBLE PRECISION, matched_manually BOOLEAN DEFAULT FALSE,
--   created_at TIMESTAMPTZ DEFAULT NOW(), updated_at TIMESTAMPTZ DEFAULT NOW(),
--   UNIQUE (user_id, external_id)           -- 중복 업로드 방지
-- );
-- -- calendar_events 요약 스칼라(1-C 표시용)
-- ALTER TABLE calendar_events ADD COLUMN IF NOT EXISTS avg_heart_rate INT;
-- ALTER TABLE calendar_events ADD COLUMN IF NOT EXISTS max_heart_rate INT;
-- ALTER TABLE calendar_events ADD COLUMN IF NOT EXISTS active_calories INT;
-- ALTER TABLE calendar_events ADD COLUMN IF NOT EXISTS avg_pace_sec_per_100m DOUBLE PRECISION;
-- ALTER TABLE calendar_events ADD COLUMN IF NOT EXISTS swim_type VARCHAR(30);


-- ============================================================
-- 검증 쿼리 (실행 후 확인)
-- ============================================================
-- 컬럼 생성 확인:
--   SELECT table_name, column_name FROM information_schema.columns
--    WHERE column_name IN ('started_at','ended_at')
--      AND table_name IN ('training_sessions','calendar_events') ORDER BY 1,2;
-- 멱등 인덱스 확인:
--   SELECT indexname FROM pg_indexes WHERE tablename='calendar_events'
--     AND indexname='ux_calendar_events_user_session';
-- stale 유니크 제거 확인(결과가 비어야 정상):
--   SELECT conname FROM pg_constraint
--    WHERE conrelid='calendar_events'::regclass AND contype='u';
