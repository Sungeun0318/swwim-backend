---
name: verify-auth
description: JWT 인증 플로우 전체 검증. "인증 확인", "로그인 검증", "토큰 확인", "JWT 검증" 후 사용.
---

# Auth 검증

## Purpose

1. 소셜 로그인 설정 (Google/Apple/Naver) 정합성
2. JWT 토큰 발급 → 저장 → 갱신 → 만료 플로우
3. 인터셉터 (401/403 처리) 동작 정합성
4. 강제 로그아웃 플로우

## When to Run

- 인증 관련 코드 수정 후
- 로그인/로그아웃 버그 발생 시
- 소셜 로그인 설정 변경 후
- 토큰 갱신 로직 수정 후

## Related Files

| File | Purpose |
|------|---------|
| `lib/core/auth/app_auth_service.dart` | 인증 싱글톤 (로그인/로그아웃/자동로그인) |
| `lib/core/api/api_client.dart` | JWT 인터셉터 + 토큰 갱신 |
| `lib/features/login/data/repositories/social_login_service.dart` | 소셜 로그인 서비스 |
| `lib/features/login/application/auth_notifier.dart` | 인증 상태 관리 |
| `lib/main.dart` | navigatorKey + 강제 로그아웃 콜백 |
| `ios/Runner/Info.plist` | iOS URL scheme (Google reversed client ID) |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/controller/AuthController.java` | 인증 API |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/service/auth/AuthService.java` | 인증 비즈니스 로직 |
| `swwim-backend/src/main/java/com/zalmuk/swwim/api/security/SecurityConfig.java` | 보안 설정 |

## Workflow

### Step 1: 소셜 로그인 설정

**Google (iOS):**

```bash
grep -n "clientId\|serverClientId" lib/core/auth/app_auth_service.dart
```

**PASS:** `GoogleSignIn(clientId: ..., serverClientId: ...)` 설정됨
**FAIL:** `GoogleSignIn()` 파라미터 없음 → 크래시 원인

**Google URL Scheme:**

```bash
grep -A2 "CFBundleURLSchemes" ios/Runner/Info.plist
```

**PASS:** `com.googleusercontent.apps.<client-id>` 존재
**FAIL:** URL scheme 누락

### Step 2: 토큰 플로우

**로그인 → 토큰 저장:**

```bash
grep -n "saveTokens\|_loginToBackend" lib/core/auth/app_auth_service.dart
```

**확인:** `_loginToBackend` 결과에서 `saveTokens(accessToken, refreshToken)` 호출되는지

**자동 로그인:**

```bash
grep -n "tryAutoLogin\|isAuthenticated" lib/core/auth/app_auth_service.dart
```

**확인:** SharedPreferences에서 사용자 정보 복원 + `isAuthenticated` 체크

### Step 3: 인터셉터 검증

```bash
grep -n "isExpiredToken\|_isRefreshing\|refreshAccessToken\|onForceLogout\|clearTokens" lib/core/api/api_client.dart
```

**확인 포인트:**
- 401 또는 403(빈 body) → 토큰 갱신 시도
- 갱신 중 요청은 대기 목록에 추가
- 갱신 성공 → 원래 요청 + 대기 요청 재시도
- 갱신 실패 → `clearTokens()` + `onForceLogout` 호출

### Step 4: 강제 로그아웃

```bash
grep -n "onForceLogout\|_forceLogout\|onForceLogoutNavigate\|navigatorKey" lib/core/auth/app_auth_service.dart lib/core/api/api_client.dart lib/main.dart
```

**확인:**
- `ApiClient.onForceLogout` → `AppAuthService._forceLogout()` 연결
- `_forceLogout()` → `_authStateController.add(null)` + 사용자 정보 삭제
- `onForceLogoutNavigate` → `navigatorKey`로 `/login` 이동

### Step 5: 백엔드 보안 설정

```bash
grep -n "permitAll\|authenticated" swwim-backend/src/main/java/com/zalmuk/swwim/api/security/SecurityConfig.java
```

**확인:**
- `/auth/login`, `/auth/refresh` → permitAll
- `/webhooks/**` → permitAll
- 나머지 → authenticated

## Output Format

```markdown
| 검사 항목 | 상태 | 상세 |
|-----------|------|------|
| Social Login Config | PASS/FAIL | Google/Apple/Naver |
| Token Flow | PASS/FAIL | login→save→refresh→expire |
| Interceptor Logic | PASS/FAIL | 401/403 handling |
| Force Logout | PASS/FAIL | clearTokens→navigate |
| Security Config | PASS/FAIL | permit paths |
```

## Exceptions

1. **Naver 로그인 iOS 미지원** — flutter_naver_login이 iOS에서 제한적일 수 있음, Android 전용이어도 정상
2. **테스트용 토큰 만료 시간** — dev 환경에서 토큰 만료가 짧을 수 있음
3. **`Authorization: ''` 빈 헤더** — 토큰 교환/갱신 요청에서 인터셉터 무시 목적, 의도된 패턴
