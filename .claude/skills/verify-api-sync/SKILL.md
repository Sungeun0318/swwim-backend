---
name: verify-api-sync
description: Flutter API 호출과 Spring Boot 엔드포인트 간 일관성 검증. "API 확인", "프론트 백엔드 맞는지 확인", "엔드포인트 확인" 후 사용.
---

# API Sync 검증

## Purpose

1. 프론트엔드 API 호출 경로 ↔ 백엔드 컨트롤러 매핑 일치 확인
2. HTTP 메서드 (GET/POST/PUT/DELETE/PATCH) 일치 확인
3. 요청/응답 필드명 일관성 확인
4. 인증 불필요 경로 정합성 확인

## When to Run

- 새 API 엔드포인트 추가/수정 후
- DTO 필드 변경 후
- "서버에서 데이터가 안 와요" 류의 버그 발생 시
- 프론트엔드 API 서비스 수정 후

## Related Files

| File | Purpose |
|------|---------|
| `lib/core/api/services/auth_api_service.dart` | 인증 API |
| `lib/core/api/services/user_api_service.dart` | 사용자 API |
| `lib/core/api/services/training_api_service.dart` | 훈련 API |
| `lib/core/api/services/calendar_api_service.dart` | 캘린더 API |
| `lib/core/api/services/community_api_service.dart` | 커뮤니티 API |
| `lib/core/api/services/pool_api_service.dart` | 수영장 API |
| `lib/core/api/services/chat_api_service.dart` | 채팅 API |
| `lib/core/api/services/storage_api_service.dart` | S3 스토리지 API |
| `lib/core/api/services/notification_api_service.dart` | 알림 API |
| `lib/core/api/services/quickstart_api_service.dart` | 퀵스타트 API |
| `lib/core/api/services/admin_api_service.dart` | 관리자 API |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/controller/` | 백엔드 컨트롤러 전체 |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/dto/` | 백엔드 DTO 전체 |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/security/SecurityConfig.java` | 보안 설정 |

## Workflow

### Step 1: 프론트엔드 API 경로 추출

Grep으로 Flutter API 서비스 파일에서 모든 API 호출 경로를 추출:

```bash
grep -rn "\.get\|\.post\|\.put\|\.delete\|\.patch" lib/core/api/services/
```

### Step 2: 백엔드 컨트롤러 매핑 추출

```bash
grep -rn "@GetMapping\|@PostMapping\|@PutMapping\|@DeleteMapping\|@PatchMapping\|@RequestMapping" swwim-backend/src/main/java/com/zalmuk/swwim/api/controller/
```

### Step 3: 경로 + 메서드 대조

프론트엔드 호출과 백엔드 매핑을 1:1 대조. 다음을 확인:
- 경로 문자열 일치
- HTTP 메서드 일치
- Path variable / Query parameter 일치

### Step 4: 필드명 일관성

CLAUDE.md 주의사항 기반 확인:
- 프로필: `nickname`, `bio`, `phoneNumber`, `profileImageUrl`
- 페이지네이션: `page=0` (0-indexed), 응답 `content[]` + `totalElements`
- 응답 래핑: `{success, data, timestamp}`

### Step 5: 인증 경로 확인

SecurityConfig의 `permitAll` 경로와 프론트엔드의 `Authorization: ''` (빈 헤더) 요청이 일치하는지 확인.

## Output Format

```markdown
| 검사 항목 | 상태 | 상세 |
|-----------|------|------|
| Endpoint Match | PASS/FAIL | N mismatches |
| HTTP Method Match | PASS/FAIL | - |
| Field Consistency | PASS/FAIL | details |
| Auth Paths | PASS/FAIL | - |
```

## Exceptions

1. **웹훅 엔드포인트** — `POST /webhooks/revenuecat`는 외부 서비스 호출이므로 프론트엔드에 없어도 정상
2. **헬스체크** — `GET /health`는 모니터링용이므로 프론트엔드에 없어도 정상
3. **관리자 전용 API** — 프론트엔드 admin 서비스에만 존재, 일반 API 서비스에 없어도 정상
