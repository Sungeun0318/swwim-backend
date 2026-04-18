---
name: verify-deploy
description: EC2 배포 전 사전 검증. "배포 확인", "배포 전 검증", "서버 올리기 전에 확인" 후 사용.
---

# Deploy 검증

## Purpose

1. 로컬 JAR 빌드 성공 확인
2. DB 스키마 변경 (Entity 변경) 감지
3. 환경 변수 / 프로퍼티 변경 감지
4. 현재 서버 상태 헬스체크

## When to Run

- 백엔드 코드 변경 후 배포 전
- 새 엔드포인트 추가 후
- DB Entity 수정 후
- 배포 실패 원인 조사 시

## Related Files

| File | Purpose |
|------|---------|
| `swwim-backend/build.gradle.kts` | Gradle 빌드 설정 |
| `swwim-backend/src/main/resources/application.properties` | 기본 설정 |
| `swwim-backend/src/main/resources/application-prod.properties` | 프로덕션 설정 |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/entity/` | JPA Entity 전체 |
| `swwim-key.pem` | EC2 SSH 키 |

## Infrastructure

- **EC2 IP**: `54.180.109.197`
- **SSH**: `ec2-user@54.180.109.197` (key: `swwim-key.pem`)
- **App Path**: `/home/ec2-user/`
- **Startup**: `/home/ec2-user/start.sh`
- **Log**: `/home/ec2-user/app.log`
- **DB DDL**: `validate` (수동 마이그레이션 필요)

## Workflow

### Step 1: JAR 빌드

```bash
cd /Users/sungeun/Developer/flutter/swwim-backend && ./gradlew bootJar -x test 2>&1 | tail -10
```

**PASS:** `BUILD SUCCESSFUL`
**FAIL:** 컴파일/빌드 에러 → 배포 불가

### Step 2: Entity 변경 감지

```bash
cd /Users/sungeun/Developer/flutter/swwim-backend && git diff HEAD~1 --name-only | grep -i entity
```

**변경 있음:** ALTER TABLE SQL 작성 필요 (DDL mode: validate)
**변경 없음:** 마이그레이션 불필요

Entity 변경 시 확인할 사항:
- 새 컬럼 추가 → `ALTER TABLE ADD COLUMN`
- 컬럼명 변경 → `ALTER TABLE RENAME COLUMN`
- 컬럼 삭제 → `ALTER TABLE DROP COLUMN` (데이터 손실 주의)
- 새 테이블 → `CREATE TABLE`

### Step 3: 프로퍼티 변경 감지

```bash
cd /Users/sungeun/Developer/flutter/swwim-backend && git diff HEAD~1 -- src/main/resources/
```

**변경 있음:** prod 프로퍼티에도 반영 필요
**변경 없음:** 추가 작업 불필요

### Step 4: 서버 헬스체크

```bash
curl -s -o /dev/null -w "%{http_code}" http://54.180.109.197:8080/api/v1/health
```

**PASS:** HTTP 200
**FAIL:** 서버 다운 또는 네트워크 문제

### Step 5: 배포 명령어 (참고)

```bash
# JAR 업로드
scp -i /Users/sungeun/Developer/flutter/swwim-key.pem \
  build/libs/*.jar ec2-user@54.180.109.197:/home/ec2-user/

# 서버 재시작
ssh -i /Users/sungeun/Developer/flutter/swwim-key.pem ec2-user@54.180.109.197 \
  'bash /home/ec2-user/start.sh'
```

### Step 6: 배포 후 검증

```bash
# 헬스체크 (30초 대기 후)
curl -s http://54.180.109.197:8080/api/v1/health

# 로그 확인
ssh -i /Users/sungeun/Developer/flutter/swwim-key.pem ec2-user@54.180.109.197 \
  'tail -50 /home/ec2-user/app.log'
```

**확인:** Hibernate validate 에러 없는지, DB 연결 정상인지

## Output Format

```markdown
| 검사 항목 | 상태 | 상세 |
|-----------|------|------|
| JAR Build | PASS/FAIL | - |
| Schema Changes | NONE/MIGRATION_NEEDED | 변경 Entity 목록 |
| Config Changes | NONE/UPDATE_NEEDED | 변경 프로퍼티 목록 |
| Server Health | PASS/FAIL | HTTP status |
| Overall | READY/NOT_READY | - |
```

## Exceptions

1. **`-x test` 스킵** — 테스트 미구현 상태이므로 정상
2. **헬스체크 타임아웃** — 네트워크 환경에 따라 간헐적 실패 가능, 재시도
3. **Entity 변경 없이 DDL 에러** — 이전 배포에서 마이그레이션 누락 가능성, `app.log` 확인
