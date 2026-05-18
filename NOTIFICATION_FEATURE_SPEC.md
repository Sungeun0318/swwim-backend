# SWWIM 알림 기능 구체화 기획서

> 작성일: 2026-05-03
> 위치: `swwim-backend/NOTIFICATION_FEATURE_SPEC.md`
> 관련 문서: `swwim/PUSH_NOTIFICATION_PLAN.md` (초기 방향성)
> 본 문서는 현재 코드 상태를 점검하고 **인앱 알림 + OS 푸시 알림(앱 미사용·종료 상태 포함)**을 어디까지, 어떤 보안 기준으로 구현할지를 결정한다.

---

## 1. 목적 / 범위

| 구분 | 설명 |
| --- | --- |
| 인앱 알림 | 앱 내부 알림함. DB 저장 → REST API 조회 → 알림 화면에 노출 |
| OS 푸시 알림 (외부 푸시) | 앱이 백그라운드/종료 상태일 때 단말 OS의 알림 센터에 노출 |
| 로컬 알림 | 백엔드 서버 없이 단말에서만 트리거하는 보조 채널 (오프라인/훈련 카운트다운 보조용) |

목표: **로그인 후 앱을 실행하지 않아도 좋아요/댓글/팔로우/훈련 리마인더 등이 잠금화면·알림 센터에 도착해야 한다.**

---

## 2. 현재 구현 상태 점검

### 2.1 백엔드 (Spring Boot)

| 항목 | 파일 | 상태 |
| --- | --- | --- |
| 인앱 알림 엔티티 | `entity/notification/Notification.java` | OK (id, type, title, body, senderId, relatedId, actionUrl, isRead, scheduledFor, sentAt) |
| 알림 타입 enum | `entity/enums/NotificationType.java` | TRAINING_REMINDER / FOLLOW / LIKE / COMMENT / ACHIEVEMENT — 운영 공지(SYSTEM) 타입 부재 |
| 알림 컨트롤러 | `controller/NotificationController.java` | 목록/미읽음/읽음/전체읽음/삭제만 존재 |
| 알림 서비스 | `service/notification/NotificationService.java` | createNotification, sendFollow/Like/CommentNotification 헬퍼 존재. **외부 푸시 발송 호출은 없음** |
| 토큰 저장 | `entity/user/UserSettings.java` 의 `fcmToken` 단일 컬럼 | **단일 디바이스 가정 → 다중 디바이스/플랫폼별 토큰 분리 불가** |
| 토큰 등록 API | `UserController` 의 `updateFcmToken` (단일) | 플랫폼/만료/마지막 사용 시각 미기록 |
| 외부 푸시 발송 | (없음) | **APNs / FCM 발송 코드 0%** |
| 스케줄러 | (없음) | `scheduledFor` 컬럼은 있으나 `@Scheduled` 발송 잡 없음 |
| 사용자 알림 세부 설정 | `enablePushNotifications`, `enableTrainingReminders` 두 종류만 | 좋아요/댓글/팔로우/마케팅 등 세분화 부재 |

### 2.2 프론트엔드 (Flutter)

| 항목 | 파일 | 상태 |
| --- | --- | --- |
| 로컬 알림 SDK | `flutter_local_notifications: ^17.2.3` | 설치됨 |
| FCM SDK | `firebase_messaging` | **미설치 → Android 외부 푸시 경로 자체가 없음** |
| iOS APNs | `AppDelegate.swift` + `MethodChannel('com.zalmuk.swwim/apns')` | 토큰 발급은 가능, 백엔드 등록은 `UserApiService().registerFcmToken` 1개로만 호출 |
| 인앱 알림 API 호출 | `core/api/services/notification_api_service.dart` | 목록/읽음/삭제는 OK |
| 푸시 토큰 등록 호출 | `notification_api_service.dart` 가 `/notifications/fcm-token` 호출 | **백엔드에 해당 엔드포인트 없음 (API 미스매치)** |
| 훈련 알림 호출 | `training_notification_service.dart` 가 `/notifications/schedule`, `/notifications/training/{id}`, `/notifications/cleanup` 호출 | **백엔드에 해당 엔드포인트 없음 (API 미스매치)** |
| 즉시 알림 저장 | `notification_service.dart` 가 `/notifications` POST | **백엔드는 GET만 존재 (API 미스매치)** |

### 2.3 OS 설정

| 항목 | 위치 | 상태 |
| --- | --- | --- |
| iOS aps-environment | `ios/Runner/Runner.entitlements` | `development` — 배포 빌드 시 `production` 으로 교체 필수 |
| iOS UIBackgroundModes | `ios/Runner/Info.plist` | `remote-notification` 포함됨 |
| Android `POST_NOTIFICATIONS` | `android/app/src/main/AndroidManifest.xml` | **미선언 → Android 13+ 에서 알림 표시 불가** |
| Android FCM `google-services.json` | (없음) | Firebase 콘솔 등록과 함께 추가 필요 |

---

## 3. 부족한 부분 / 보강 필요 항목

### 3.1 기능 누락
1. **외부 푸시 발송기 부재** — 백엔드에서 APNs(JWT) 또는 FCM HTTP v1 호출하는 컴포넌트가 0
2. **Android FCM 토큰 발급 경로 부재** — `firebase_messaging` 미설치
3. **다중 디바이스 미지원** — 사용자가 폰 + 패드 같이 쓰면 한쪽만 동작
4. **토큰 만료 처리 없음** — APNs/FCM이 “Unregistered” / “InvalidToken” 응답 시 비활성화 로직 없음
5. **스케줄링 잡 부재** — 훈련 30분/1시간 전 리마인더 발송 워커 없음
6. **API 미스매치** — 프론트가 호출하는 5개 이상 엔드포인트가 백엔드에 없음
7. **세분화된 사용자 설정 없음** — “좋아요는 끄고 댓글만 받음” 같은 채널별 on/off 미지원
8. **마케팅/서비스 알림 미분리** — 운영 공지/이벤트성 알림을 보내려면 별도 동의 채널 필요
9. **딥링크 규칙 미정** — `actionUrl` 포맷이 정해지지 않음 (예: `swwim://post/{id}`, `swwim://user/{id}`)
10. **읽음 동기화** — OS 푸시 클릭 → 인앱 `isRead` 자동 처리 로직 없음

### 3.2 보안/개인정보 누락
1. **알림 페이로드에 민감정보 포함 위험** — APNs/FCM 페이로드는 외부 게이트웨이를 거치므로 본문에 개인정보·이메일·전화번호·전체 주소를 절대 넣지 않는다.
2. **권한 검증** — `markAsRead`, `getUnreadCount` 등에서 `@AuthenticationPrincipal String userId` 만 사용. `markAsRead(UUID id)` 가 본인 알림인지 검증하지 않음 (delete는 검증함). **본인 검증 일관성 없음**.
3. **토큰 등록 인증** — `/users/{userId}/fcm-token` 같은 PathVariable userId 호출 구조 시 본인이 아닌 토큰 갈아치우기 가능. **토큰 등록은 반드시 인증된 사용자 본인에게만**.
4. **Rate limit 부재** — 스팸 댓글/좋아요로 알림 폭격 가능. IP/사용자 단위 발송 제한 없음.
5. **JWT 시크릿 관리** — APNs용 .p8 키, FCM 서비스 계정 JSON을 어디에 저장할지(환경변수 / AWS Secrets Manager) 결정 필요.
6. **로그아웃 시 토큰 폐기** — 현재 코드에 로그아웃 토큰 삭제 흐름 없음 → 다른 사용자가 같은 단말 로그인 시 이전 사용자 알림 수신 가능.
7. **계정 삭제 시 토큰/알림 cascade 삭제** — `notificationRepository.deleteByUser` 는 있으나 토큰 cascade 미정의.
8. **개인정보처리방침 반영** — 디바이스 토큰 수집·보관·발송 목적·수신 거부 방법 명시 필요.
9. **마케팅성 알림 동의** — 정보통신망법상 별도 동의·수신 거부 안내 필수.
10. **TLS / 인증서 검증** — APNs/FCM 호출 모두 HTTPS, 서버 측 인증서 검증을 끄지 않는다.

---

## 4. 목표 아키텍처

```
[클라이언트 이벤트]
  · 좋아요/댓글/팔로우 (Spring Boot 비즈니스 로직)
  · 훈련 리마인더 스케줄러 (@Scheduled, 1분 단위)
  · 운영자 수동 발송 (관리자 API)
        |
        v
[NotificationService.create() — 인앱 알림 DB 저장]
        |
        v
[PushDispatcher — 비동기 (Spring Events / @Async)]
        | 사용자 설정 / 채널별 on/off / 마케팅 동의 체크
        v
[UserPushTokenRepository] 활성 토큰 조회 (platform별)
        |
   ┌────┴────┐
   v         v
[ApnsClient] [FcmClient]
   |         |
   v         v
 APNs       FCM
   |         |
   v         v
 iPhone    Android
```

핵심 원칙:
- **인앱 저장 → 외부 발송**은 분리. 외부 발송 실패해도 인앱 알림은 남는다.
- 외부 발송은 **비동기**. 본 비즈니스 트랜잭션을 절대 블로킹/롤백시키지 않는다.
- 발송 결과는 **푸시 발송 로그 테이블**에 기록한다 (운영/감사).

---

## 5. 데이터 모델 (변경/신규)

### 5.1 신규: `user_push_tokens`
> 기존 `user_settings.fcm_token` 단일 컬럼은 deprecated 처리, 마이그레이션 후 제거.

```sql
CREATE TABLE user_push_tokens (
  id UUID PRIMARY KEY,
  user_id VARCHAR(128) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  platform VARCHAR(10) NOT NULL,        -- 'ios' | 'android'
  provider VARCHAR(10) NOT NULL,        -- 'apns' | 'fcm'
  token TEXT NOT NULL,
  device_id VARCHAR(128),               -- 멀티 디바이스 식별 (옵션)
  app_version VARCHAR(20),
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  last_used_at TIMESTAMP,
  failure_count INT NOT NULL DEFAULT 0, -- 발송 실패 누적, 임계치 초과 시 enabled=false
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  UNIQUE(user_id, token)
);
CREATE INDEX idx_push_tokens_user_enabled ON user_push_tokens(user_id, enabled);
```

### 5.2 신규: `push_dispatch_logs` (감사·운영용)
```sql
CREATE TABLE push_dispatch_logs (
  id UUID PRIMARY KEY,
  notification_id UUID,                 -- 인앱 Notification id (nullable: 인앱 없는 발송도 가능)
  user_id VARCHAR(128) NOT NULL,
  token_id UUID,
  provider VARCHAR(10),
  status VARCHAR(20),                   -- 'sent' | 'failed' | 'invalid_token' | 'rate_limited'
  error_code VARCHAR(50),
  error_message TEXT,
  sent_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_push_logs_user_time ON push_dispatch_logs(user_id, sent_at DESC);
```

### 5.3 변경: `user_settings`
세분화 필드 추가. 기본값은 모두 `true`이되 마케팅만 `false`(옵트인).

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `notify_like` | boolean | 좋아요 알림 |
| `notify_comment` | boolean | 댓글 알림 |
| `notify_follow` | boolean | 팔로우 알림 |
| `notify_training_reminder` | boolean | (기존 `enable_training_reminders` 재사용 가능) |
| `notify_achievement` | boolean | 업적 알림 |
| `notify_system` | boolean | 운영 공지 (강제 ON 권장) |
| `notify_marketing` | boolean | 마케팅성 알림 (기본 OFF, 별도 동의 시 ON) |
| `marketing_agreed_at` | timestamp | 마케팅 수신 동의 시각 (정보통신망법 증빙) |
| `quiet_hours_start` | time | 방해 금지 시작 (옵션) |
| `quiet_hours_end` | time | 방해 금지 종료 (옵션) |

### 5.4 변경: `NotificationType` enum
- `SYSTEM` (운영 공지) 추가
- `MARKETING` (이벤트/프로모션) 추가
- 클라이언트 라우팅을 위해 `category` (social / training / system / marketing) 분류 도입 권장

---

## 6. API 명세 (목표)

### 6.1 푸시 토큰
```
POST   /api/v1/me/push-tokens               # 등록(또는 갱신: token unique)
DELETE /api/v1/me/push-tokens/{id}          # 단건 폐기
DELETE /api/v1/me/push-tokens?device=...    # 로그아웃 시 디바이스별 폐기
GET    /api/v1/me/push-tokens               # (관리/디버그용) 본인 토큰 목록
```
요청 예시
```json
POST /api/v1/me/push-tokens
{ "platform": "ios", "provider": "apns", "token": "abc...", "deviceId": "uuid", "appVersion": "1.4.0" }
```
보안 규칙
- 인증 필수 (JWT)
- 본인 토큰만 등록/조회/삭제 (Path/Query의 userId 신뢰 금지, JWT의 sub 만 사용)
- 동일 token 재등록은 upsert (`last_used_at` 갱신, `enabled=true` 복원)

### 6.2 알림함 (기존 + 보강)
- 기존: `GET /notifications`, `GET /notifications/unread`, `GET /notifications/unread/count`, `POST /notifications/{id}/read`, `POST /notifications/read-all`, `DELETE /notifications/{id}`
- **보강**: `markAsRead(UUID)` 시 본인 알림 검증 (현재 누락) — 다른 사람 알림 ID 추측해서 읽음 처리 못하게.

### 6.3 훈련 리마인더 스케줄링 (프론트가 이미 호출 중인 미스매치 해결)
```
POST   /api/v1/notifications/schedule
       body: { "notifications": [ { trainingId, scheduledTime, message, reminderMinutes } ] }
DELETE /api/v1/notifications/training/{trainingId}
POST   /api/v1/notifications/cleanup       # 만료 정리 (또는 백그라운드 잡으로 대체 후 deprecate)
```
- 또는 클라이언트 호출을 없애고, **훈련 생성/수정 API에서 서버가 자동 스케줄**하는 방향으로 단순화 권장.

### 6.4 관리자 발송 (운영자 전용)
```
POST   /api/v1/admin/notifications/broadcast
       body: { audience: 'all'|'user_ids'|'segment', type: 'SYSTEM'|'MARKETING', title, body, deepLink }
```
- 권한: ADMIN 롤만 (이미 있는 Role 시스템 사용)
- 마케팅 발송은 `notify_marketing=true && marketing_agreed_at` 사용자만 대상

---

## 7. 외부 푸시 발송 설계

### 7.1 발송 방식 선택
| 옵션 | 장점 | 단점 | 권장 |
| --- | --- | --- | --- |
| **A. 백엔드 직접 호출** (APNs HTTP/2 + JWT, FCM HTTP v1 + Service Account) | 응답 코드 직접 제어, 의존성 적음 | JWT 갱신·HTTP/2 커넥션 풀 등 직접 구현 | 초기 V1 |
| B. AWS SNS Platform Application | AWS 한 곳 관리, 토큰 endpoint ARN 통합 | endpoint 정리·SNS 사용량 비용 추가 | 트래픽 커지면 검토 |

V1은 **A안 직접 호출**로 가는 것을 권장. Spring Boot 전용 라이브러리:
- APNs: `com.eatthepath:pushy` 또는 자체 구현 (HTTP/2 + ECDSA JWT)
- FCM: `com.google.firebase:firebase-admin` (서비스 계정 JSON 사용)

### 7.2 시크릿 관리
- APNs `.p8` 키, Team ID, Key ID
- FCM 서비스 계정 JSON
- **저장 위치**: 환경변수 + 운영은 AWS Secrets Manager / Parameter Store. 절대 git 커밋 금지.
- application-prod.properties는 `${APNS_KEY_PATH}`, `${FCM_SERVICE_ACCOUNT_PATH}` 형태로 참조.

### 7.3 페이로드 설계 (보안 우선)

**원칙**: 잠금화면에 노출되어도 안전한 정도로만 본문에 담는다.

iOS 예시:
```json
{
  "aps": {
    "alert": { "title": "좋아요", "body": "지영님이 게시글을 좋아합니다." },
    "sound": "default",
    "badge": 3,
    "category": "LIKE",
    "mutable-content": 1
  },
  "type": "LIKE",
  "notificationId": "uuid",
  "deepLink": "swwim://post/{postId}",
  "senderId": "uuid"
}
```

Android 예시:
```json
{
  "message": {
    "token": "...",
    "notification": { "title": "좋아요", "body": "지영님이 게시글을 좋아합니다." },
    "data": { "type": "LIKE", "notificationId": "uuid", "deepLink": "swwim://post/{postId}" },
    "android": { "priority": "HIGH", "notification": { "channel_id": "swimming_starter_high" } }
  }
}
```

금지: 이메일, 전화번호, 주소, 비밀번호, 결제 정보, JWT, 본문 전체. **id 만 페이로드에 싣고 상세는 클릭 시 인앱 API로 조회**.

### 7.4 딥링크 규칙
| URL | 화면 |
| --- | --- |
| `swwim://post/{postId}` | 게시글 상세 |
| `swwim://user/{userId}` | 프로필 |
| `swwim://comment/{postId}?focus={commentId}` | 댓글 포커스 |
| `swwim://training/{trainingId}` | 훈련 상세 |
| `swwim://notice/{noticeId}` | 운영 공지 |

### 7.5 실패 처리
- APNs 응답 `Unregistered` / FCM `UNREGISTERED`, `INVALID_ARGUMENT(token)` → `enabled=false`
- 그 외 일시 오류 → `failure_count++`, 임계치(예: 5) 초과 시 비활성화
- 모두 `push_dispatch_logs` 에 기록

### 7.6 Rate limit
- 사용자당 분당 외부 푸시 N회 (예: 10) 제한 — 같은 사용자에게 알림 폭격 방지
- 동일 (userId, type, relatedId, dedupKey) 의 연속 발송은 1분 내 1회로 dedup

---

## 8. 앱 미사용 시(백그라운드/종료) 동작 흐름

### 8.1 iOS
1. 앱 최초 실행 → `requestPermission` (이미 구현)
2. APNs 토큰 발급 → `POST /api/v1/me/push-tokens`
3. 백엔드가 APNs HTTP/2로 `aps.alert` 페이로드 발송
4. 단말이 잠금화면/알림센터에 표시 (앱이 죽어 있어도 OS가 표시)
5. 사용자가 알림 탭 → 앱이 cold start → `getInitialMessage` 핸들러가 `deepLink` 따라 라우팅
6. 앱이 인앱 API에 `markAsRead(notificationId)` 호출

체크 항목
- `aps-environment`: dev 빌드는 `development`, App Store 빌드는 `production` (현재 development 고정 → 빌드 설정으로 분기 필요)
- Apple Developer 콘솔에서 Push Notifications capability 활성화
- TestFlight 빌드는 production APNs 사용

### 8.2 Android
1. `firebase_messaging` 설치 + `google-services.json` 추가
2. `AndroidManifest.xml` 에 `<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>` 추가 (Android 13+ 필수)
3. 앱 실행 시 `FirebaseMessaging.instance.requestPermission()` (Android 13+ 런타임 권한)
4. FCM 토큰 발급 → `POST /api/v1/me/push-tokens` (provider=fcm)
5. 백엔드가 FCM HTTP v1로 `notification + data` 페이로드 발송
6. 앱이 죽어 있어도 시스템 알림으로 표시 (지정한 channel_id 의 importance=HIGH)
7. 사용자가 탭 → intent extras 에서 `deepLink` 추출 → 라우팅 → `markAsRead`

체크 항목
- 알림 채널 ID `swimming_starter_high` 는 이미 코드에 있으나, FCM 발송 시 동일 channel_id 지정 필요
- 배터리 최적화 화이트리스트 (제조사별 OEM 이슈) — 도움말로 안내

### 8.3 로컬 알림 vs OS 푸시
- 훈련 카운트다운/세트 알림처럼 **앱이 떠 있을 때만** 필요한 알림은 `flutter_local_notifications` 로 단말에서 처리
- 좋아요/댓글/팔로우/리마인더처럼 **앱이 죽었을 때도** 필요한 건 반드시 APNs/FCM 경유

---

## 9. 보안 요건 (요약)

| 영역 | 요건 |
| --- | --- |
| 인증 | 모든 알림/토큰 API는 JWT 필수. 토큰 등록/조회/삭제는 JWT의 `sub` 만 신뢰 (PathVariable userId 신뢰 금지) |
| 인가 | 알림 단건 작업(read, delete)은 소유자 검증. `markAsRead` 에 누락된 본인 검증 추가 |
| 페이로드 | 외부 푸시 본문에는 식별자만, 민감정보 금지. 상세는 인앱 조회 |
| 토큰 라이프사이클 | 로그인 시 등록, 로그아웃 시 폐기, 만료 응답 시 비활성화, 계정 삭제 시 cascade 삭제 |
| 시크릿 | APNs .p8 / FCM 서비스 계정 JSON 은 환경변수 또는 AWS Secrets Manager. git 커밋 금지 |
| 통신 | APNs/FCM 모두 TLS, 인증서 검증 비활성화 금지 |
| 스팸/악용 | 사용자당 분당 N건 발송 제한, dedup, 동일 사용자 본인 발신 금지 (이미 like/comment에서 처리) |
| 마케팅 | 정보통신망법 — 별도 옵트인, 수신 동의 시각 저장, 본문에 발신자/수신거부 안내 |
| 관리자 | broadcast는 ADMIN 롤 + 감사 로그 필수 |
| 개인정보 | 처리방침에 “디바이스 토큰 수집 / 발송 목적 / 보유 기간 / 거부 방법” 명시 |

---

## 10. 구현 순서 (마일스톤)

### Phase 1 — API 미스매치 정리 + 토큰 모델 확장 (1주)
- `user_push_tokens` 테이블 생성 + 마이그레이션 (기존 `fcmToken` 데이터 이관 후 컬럼 deprecate)
- `POST/DELETE /api/v1/me/push-tokens` 구현
- `notification_api_service.dart` 호출 경로 새 엔드포인트로 정리
- `markAsRead` 에 본인 검증 추가
- 사용자 설정 세분화 (`notify_like`, `notify_comment`, ...) 추가

### Phase 2 — 외부 푸시 발송 (2주)
- Spring Boot에 `PushDispatcher` 컴포넌트 추가 (`@Async`)
- APNs HTTP/2 + JWT 클라이언트 구현 (pushy 라이브러리 권장)
- FCM HTTP v1 클라이언트 구현 (`firebase-admin`)
- `NotificationService.sendXxxNotification` 끝에 dispatcher 호출
- 실패 응답 코드 분기 → 토큰 비활성화
- `push_dispatch_logs` 적재

### Phase 3 — Android FCM SDK 통합 (1주)
- `firebase_messaging` 설치, Firebase 콘솔 등록, `google-services.json`
- `AndroidManifest.xml` 에 `POST_NOTIFICATIONS` 추가
- Android 13+ 런타임 권한 요청
- 토큰 발급 → `/me/push-tokens` 등록
- 알림 채널 `swimming_starter_high` 와 FCM 발송 channel_id 일치

### Phase 4 — 훈련 리마인더 스케줄러 (1주)
- `@Scheduled(fixedDelay=60_000)` 으로 `findPendingScheduledNotifications` 폴링
- 도달한 알림 → 인앱 + 외부 푸시 발송 + `sentAt` 마킹
- 클라이언트의 `/notifications/schedule`, `/notifications/training/{id}` 호출은 백엔드가 훈련 CRUD에서 자동 처리하도록 정리

### Phase 5 — 관리자 broadcast / 마케팅 (3일)
- `POST /api/v1/admin/notifications/broadcast` (ADMIN 롤)
- 마케팅 옵트인 동의 화면 + `marketing_agreed_at` 저장
- 발송 본문에 수신거부 안내 포함

### Phase 6 — 운영/검증 (지속)
- 잠금화면 푸시 동작을 iOS/Android 실기기에서 검증 (TestFlight, internal testing)
- iOS Production aps-environment 빌드 분리
- 배터리 최적화 OEM 이슈 안내 페이지 작성
- 스팸 알림 모니터링 (push_dispatch_logs 기반 대시보드)

---

## 11. 검증 체크리스트

### 기능
- [ ] 로그인 후 iOS 단말 토큰이 `user_push_tokens` 에 platform=ios, provider=apns 로 저장된다
- [ ] 로그인 후 Android 단말 토큰이 platform=android, provider=fcm 으로 저장된다
- [ ] 로그아웃 시 해당 디바이스 토큰이 비활성화/삭제된다
- [ ] 좋아요/댓글/팔로우 발생 시 인앱 알림이 DB에 저장되고, 외부 푸시도 발송된다
- [ ] 외부 푸시는 앱이 백그라운드 / 종료 상태일 때 잠금화면에 도착한다
- [ ] 알림 탭하면 deep link로 해당 화면이 열리고 `isRead`가 true로 변한다
- [ ] 사용자가 좋아요 알림을 끄면 좋아요 외부 푸시가 발송되지 않는다 (인앱은 정책 결정)
- [ ] 훈련 30분/1시간 전 리마인더가 백엔드 스케줄러로 발송된다
- [ ] 만료된 토큰은 응답 후 자동 비활성화 → 다음 발송 대상에서 제외된다

### 보안
- [ ] 다른 사용자의 알림 ID 로 `markAsRead`/`delete` 호출 시 403 반환
- [ ] 토큰 등록 API 가 JWT의 `sub` 외 값을 신뢰하지 않는다
- [ ] 외부 푸시 페이로드에 이메일/전화번호/주소가 없다
- [ ] APNs `.p8` / FCM 서비스 계정 JSON 이 git에 커밋되어 있지 않다
- [ ] 마케팅 알림은 옵트인 사용자에게만 발송된다
- [ ] 발송 로그가 `push_dispatch_logs` 에 기록된다
- [ ] 동일 (user, type, relatedId) 1분 내 중복 발송이 dedup 된다

### 스토어 심사
- [ ] iOS 배포 빌드의 `aps-environment` 가 `production` 으로 빌드된다
- [ ] Android 13+ 런타임 권한 다이얼로그가 정상 노출된다
- [ ] 개인정보처리방침에 디바이스 토큰 수집/이용/거부 방법이 기재되어 있다
- [ ] 알림 권한 거부 시에도 앱 핵심 기능(훈련 시작 등)이 동작한다

---

## 12. 결정이 필요한 항목 (오픈 이슈)

1. **방해금지 시간(quiet hours)** — 훈련 리마인더는 무시할지, 마케팅만 차단할지
2. **백엔드 직접 발송 vs AWS SNS** — V1은 직접 발송 권장. 트래픽 커지면 재검토
3. **로컬 알림과 OS 푸시 중복 처리** — 훈련 리마인더가 양쪽으로 가지 않도록 단일 채널로 통일할지
4. **인앱 알림 보존 기간** — 무한 보관할지, 6개월 / 1년 정책으로 자동 정리할지
5. **Push Subject(SYSTEM) 강제 ON** — 약관 변경, 보안 이슈는 사용자가 끄지 못하게 할지
6. **iOS Notification Service Extension** — 이미지 첨부/암호화 페이로드 복호화가 필요해지면 확장 추가 검토

---

## 13. 참고 파일 인덱스

백엔드:
- `src/main/java/com/zalmuk/swwim/api/controller/NotificationController.java`
- `src/main/java/com/zalmuk/swwim/api/service/notification/NotificationService.java`
- `src/main/java/com/zalmuk/swwim/api/entity/notification/Notification.java`
- `src/main/java/com/zalmuk/swwim/api/entity/enums/NotificationType.java`
- `src/main/java/com/zalmuk/swwim/api/entity/user/UserSettings.java`
- `src/main/java/com/zalmuk/swwim/api/dto/auth/LoginRequest.java` (fcmToken 필드)

프론트엔드:
- `lib/services/notification_service.dart`
- `lib/services/training_notification_service.dart`
- `lib/core/api/services/notification_api_service.dart`
- `lib/core/api/services/user_api_service.dart` (registerFcmToken)
- `ios/Runner/AppDelegate.swift`
- `ios/Runner/Runner.entitlements`
- `ios/Runner/Info.plist`
- `android/app/src/main/AndroidManifest.xml`

문서:
- `swwim/PUSH_NOTIFICATION_PLAN.md` (초기 방향성 문서, 본 기획서로 대체/심화)
