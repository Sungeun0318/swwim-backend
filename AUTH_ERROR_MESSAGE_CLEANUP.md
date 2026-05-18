# 로그인 / 회원가입 에러 메시지 정리 기획

> 작성일: 2026-05-05
> 위치: `swwim-backend/AUTH_ERROR_MESSAGE_CLEANUP.md`
> 상태: 기획 단계 — **아직 코드 변경 없음**. 결정 항목 확정 후 작업 시작.

## 1. 문제 요약

사용자에게 다음과 같은 raw 메시지가 노출되고 있다.

- Apple 로그인 **취소**도 `'Apple 로그인 중 오류가 발생했습니다: SignInWithAppleAuthorizationException...'` 같이 **에러로 표시됨**
- 이메일 로그인 실패 시 `'로그인 중 오류가 발생했습니다: DioException [bad response]: ...'` 처럼 SDK 메시지가 노출됨
- 회원가입 실패 시 백엔드의 raw `serverMsg` 또는 `$e` 가 그대로 표시됨
- 다른 앱들처럼 **취소 / 실패만 짧게 한 줄로** 보여주면 됨

## 2. 원인 분석 (코드 위치)

### 2.1 Apple 취소가 “오류”로 분류되는 이유
`lib/core/auth/app_auth_service.dart:131-167`
- Google/Naver는 사용자가 취소하면 `null` 반환 → repository가 `cancelled`로 매핑 → 스낵바 안 뜸 (정상)
- Apple은 `SignInWithApple.getAppleIDCredential()`이 `SignInWithAppleAuthorizationException(code: canceled)` **예외**를 던짐
- `app_auth_service.dart:163` 의 catch가 모든 예외를 `rethrow` → repository의 catch에서 `AuthResult.failed`로 묶임 → 사용자에게 “오류” 표시

### 2.2 raw 예외 메시지 노출
`lib/features/login/data/repositories/auth_repository.dart`
- `:119` `'로그인 중 오류가 발생했습니다: $e'`
- `:132` `'구글 로그인 중 오류가 발생했습니다: $e'`
- `:145` `'Apple 로그인 중 오류가 발생했습니다: $e'`
- `:158` `'네이버 로그인 중 오류가 발생했습니다: $e'`

### 2.3 회원가입 raw 메시지 노출
`lib/features/login/presentation/widgets/signup_modal.dart:114-126`
- `serverMsg` 그대로 노출
- catch-all `'예기치 못한 오류가 발생했습니다: $e'`

`lib/features/login/presentation/screens/signgup_sc.dart:179-191`
- 동일 패턴

## 3. 메시지 통일안

| 상황 | 변경 후 사용자 표시 메시지 |
| --- | --- |
| 소셜 로그인 취소 (Google/Apple/Naver) | (옵션 A) 표시 안 함 / (옵션 B) `로그인이 취소되었습니다.` |
| 소셜 로그인 실패 (네트워크/서버) | `로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.` |
| 이메일 로그인 401 (자격 증명 오류) | (옵션 A) `로그인에 실패했습니다.` / (옵션 B) `이메일 또는 비밀번호가 올바르지 않습니다.` |
| 이메일 로그인 그 외 실패 | `로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.` |
| 회원가입 실패 (일반) | `회원가입에 실패했습니다.` |
| 회원가입 실패 (이메일 중복 등 화이트리스트) | 백엔드 메시지 그대로 표시 (단, 사용자에게 도움이 되는 항목만) |
| 비밀번호 불일치 / 약관 미동의 | 기존 메시지 유지 (사용자 입력 검증) |
| Apple 미지원 플랫폼 | 기존 `Apple 로그인은 iOS/macOS에서만 지원됩니다.` 유지 |

원칙
- 사용자 UI에는 **raw `$e`, `DioException`, 스택 트레이스, 영문 SDK 메시지 노출 금지**
- 디버그 로그(`debugPrint`)에는 그대로 남겨 운영 디버깅에 사용
- 기능을 망가뜨리지 않는 선에서만 메시지를 단순화

## 4. 변경 파일 / 변경 포인트 (작업 시 적용)

### 4.1 `lib/core/auth/app_auth_service.dart`
- `signInWithApple` 의 catch에서 `SignInWithAppleAuthorizationException` 이면서 `code == AuthorizationErrorCode.canceled` 인 경우 → **`null` 반환** (Google과 동일하게 cancel을 표현)
- 그 외 예외만 `rethrow`

### 4.2 `lib/features/login/data/repositories/auth_repository.dart`
- `signInWithEmail` catch
  - `DioException` && `statusCode == 401` → `이메일 또는 비밀번호가 올바르지 않습니다.` 또는 `로그인에 실패했습니다.` (옵션 결정 필요)
  - 그 외 → `로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.`
  - **raw `$e` 연결 제거**
- `signInWithGoogle / signInWithApple / signInWithNaver` catch
  - 모두 `로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.`
  - raw `$e` 제거
- `_processUser` 의 cancelled 메시지(`로그인이 취소되었습니다.`)는 유지

### 4.3 `lib/features/login/application/auth_notifier.dart`
- 현재 cancelled 분기는 `errorMessage` 를 비우고 unauthenticated 로 복귀하도록 되어 있음 → 그대로 유지하면 스낵바 안 뜸 (옵션 A)
- 옵션 B (취소도 토스트)를 선택하면 cancelled 분기에서 `errorMessage = '로그인이 취소되었습니다.'` 로 셋

### 4.4 `lib/features/login/presentation/widgets/signup_modal.dart`
- `_handleSubmit` catch
  - `DioException`: `serverMsg` 를 **화이트리스트(또는 백엔드 에러 코드)**에 매칭되는 경우만 표시, 그 외는 `회원가입에 실패했습니다.`
  - catch-all: `회원가입에 실패했습니다.`
  - raw `$e` 노출 제거

### 4.5 `lib/features/login/presentation/screens/signgup_sc.dart`
- `_signUp` catch — 4.4와 동일 정책 적용

### 4.6 `login_sc.dart`
- 별도 변경 없음 (`errorMessage` 를 그대로 띄우는 listener라, 위 단계들이 끝나면 자동으로 정리됨)

## 5. 결정 필요 항목

- [ ] **취소 시 스낵바 표시 여부**
  - (A) 표시 안 함 — 사용자가 의도적으로 닫은 거니 깔끔. 다수 앱이 이 방식 (권장)
  - (B) `로그인이 취소되었습니다.` 한 번만 표시
- [ ] **이메일 로그인 401 분리 여부**
  - (A) 모두 `로그인에 실패했습니다.` — 이메일 존재 여부 노출 안 함, 보안적으로 안전
  - (B) 401만 `이메일 또는 비밀번호가 올바르지 않습니다.` — UX는 더 친절
- [ ] **회원가입 화이트리스트 키 정의** — 백엔드가 어떤 에러 코드/메시지를 던지는지 확인 후 다음 같은 매핑 표 작성
  - 이메일 중복 → `이미 사용 중인 이메일입니다.`
  - 닉네임 중복 → `이미 사용 중인 닉네임입니다.`
  - 비밀번호 형식 → `비밀번호 형식이 올바르지 않습니다.`
  - 그 외 → `회원가입에 실패했습니다.`

## 6. 검증 체크리스트

- [ ] Apple 로그인 시트에서 취소 → 스낵바 표시 안 됨 (또는 “로그인이 취소되었습니다.”만)
- [ ] Google 로그인 시트에서 취소 → 동일 동작
- [ ] Naver 로그인 시트에서 취소 → 동일 동작
- [ ] 이메일 로그인 잘못된 비밀번호 → `이메일 또는 비밀번호가 올바르지 않습니다.` (혹은 통합 메시지)
- [ ] 이메일 로그인 네트워크 끊김 → `로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.`
- [ ] 회원가입 이메일 중복 → 화이트리스트 메시지
- [ ] 회원가입 비밀번호 불일치 → 기존 검증 메시지 유지
- [ ] 회원가입 약관 미동의 → 기존 검증 메시지 유지
- [ ] 회원가입 알 수 없는 오류 → `회원가입에 실패했습니다.` (raw `$e` 노출 X)
- [ ] 디버그 로그(`debugPrint`)에는 원본 예외가 그대로 남아 있다 (운영 디버깅 가능)

## 7. 작업 우선순위

1. (즉시) Apple 취소 → null 반환 변환 (4.1) — 가장 두드러지는 버그
2. (즉시) repository catch에서 raw `$e` 제거 (4.2)
3. (즉시) 회원가입 catch에서 raw `$e` 제거 (4.4, 4.5)
4. (이후) 백엔드 에러 코드 구조 확인 후 회원가입 화이트리스트 적용
5. (이후) 401 분리 / 취소 토스트 표기 등 UX 옵션 결정 반영
