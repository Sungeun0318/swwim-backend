# Apple App Review 거절 대응 기획 (2026-05-04 재거절)

> Submission ID: `ee8f38e4-2ec2-4116-a936-90b8f689c275`
> Review date: 2026-05-04
> Version reviewed: 1.0.3 (42)
> Review devices: iPad Pro 11-inch (M4) — iPadOS 26.4.2, iPhone 17 Pro Max — iOS 26.4.1
> 상태: 기획 단계 — **코드/메타데이터 변경 없음**. 결정 항목 확정 후 작업 시작.
> 관련 문서: `APP_REVIEW_REJECTION_GUIDE.md` (이전 거절 1차 가이드), `RESUBMISSION_CHECKLIST.md` (메타데이터 체크리스트)

---

## 거절 사유 요약

| # | Guideline | 영역 | 작업 위치 |
| --- | --- | --- | --- |
| 1 | **2.1(a)** App Completeness | 로그인 오류 (iPad/iPhone) | Flutter 코드 + iOS 설정 |
| 2 | **2.1(b)** App Completeness | IAP 구매 시 에러 | App Store Connect + Flutter 코드 |
| 3 | **3.1.2(c)** Subscriptions | EULA 링크 누락 | App Store Connect (메타데이터) |

---

## 1. 거절 #1 — Guideline 2.1(a): Log in error

### 1.1 Apple 지적
> The app exhibited one or more bugs that would negatively impact users.
> Bug description: **Log in error**
> Devices: iPad Pro 11-inch (M4) iPadOS 26.4.2, iPhone 17 Pro Max iOS 26.4.1

### 1.2 의심되는 원인 (가장 가능성 높은 순)

#### A. iPad에서 소셜 로그인 화면(시트) 좌표/표시 이슈
- `sign_in_with_apple` / `google_sign_in` SDK 모두 iPad에서 popover anchor를 요구하는 경우가 있음
- iPadOS 26 에서 Apple 로그인 시트가 안 뜨거나, presentation context 가 nil 이면 즉시 실패
- 현재 `lib/features/login/presentation/screens/login_sc.dart` 가 **iPhone 폭 기준으로 설계**되어 iPad에서 카드/버블이 어긋남 (절대 좌표 사용)

#### B. APNs MethodChannel 실패가 로그인 흐름을 차단할 가능성
- `lib/services/notification_service.dart` 의 `registerCurrentDeviceToken` 이 로그인 직후 `_apnsChannel.invokeMethod('requestPermission')` / `getToken` 을 호출
- iPad/iOS 26 에서 권한 거부 시 또는 simulator-like 환경에서 토큰이 안 와 무한 대기
- 토큰 등록은 인증 자체와 분리되어 있어야 하지만, 현재 구조에서 막히면 로그인 후 후속 동작이 멎을 수 있음

#### C. iOS 26 / iPadOS 26 SDK 호환성
- `purchases_flutter`, `google_sign_in`, `sign_in_with_apple` 의 버전이 iOS 26 SDK 와 충돌하지 않는지 확인 필요
- `pubspec.yaml` 의존성 버전 점검 필요 (현재 미확인)

#### D. 로그인 에러 메시지 자체가 거절 사유일 가능성
- 별도 기획서 `AUTH_ERROR_MESSAGE_CLEANUP.md` 에서 다룬 raw 예외 노출 문제
- Apple 리뷰어가 “로그인 오류 메시지가 떴다”라고 표현했을 수 있음 — 이 경우 메시지 정리가 함께 필요

#### E. 자동 로그인 만료 / 토큰 리프레시 실패
- `app_auth_service.dart:tryAutoLogin` → 토큰 만료 시 force logout. 리뷰 환경처럼 시간 지난 빌드 사용 시 토큰이 죽어 있을 수 있음

### 1.3 조치 계획

#### Phase A. 재현 (사전 작업)
- [ ] iPad Pro 11" (M4) 시뮬레이터 / iPhone 17 Pro Max 시뮬레이터에서 iOS 26.4 SDK로 빌드
- [ ] TestFlight 빌드를 실제 iPad 단말에서 테스트 (가능 시)
- [ ] 로그인 시도 단계별 로그 확인: 화면 진입 → 버튼 탭 → SDK 시트 → 백엔드 호출 → JWT 저장 → 다음 화면 라우팅
- [ ] 이메일 / Google / Apple 각각 어디서 막히는지 분리 확인

#### Phase B. 수정 후보 (재현 결과에 따라 선택)

**B-1. iPad presentation context 보강**
- Apple 로그인의 경우 iPad는 `webAuthenticationOptions` presentationAnchor 가 필요한 케이스 있음 — iOS 26 에서 강화됐을 가능성
- `sign_in_with_apple` 패키지 최신 버전 확인 + iPad anchor 처리

**B-2. 로그인 화면 iPad 레이아웃 대응**
- `login_sc.dart` 에 가로 폭 기반 분기 (예: 600pt 이상 → 카드 max width 480, 절대 좌표 버블 비활성화)
- iPad 가로/세로 회전 모두에서 카드가 화면 안에 들어오는지 확인

**B-3. 알림 토큰 등록 분리**
- `notification_service.registerCurrentDeviceToken()` 호출을 로그인 성공 후 fire-and-forget 으로 보내고, 결과를 기다리지 않도록 정리
- 실패해도 다음 화면 라우팅이 진행되어야 함 (현재도 그렇게 보이지만 타이밍 확인)

**B-4. `AUTH_ERROR_MESSAGE_CLEANUP.md` 적용**
- 별도 기획대로 raw `$e` / `DioException` 노출 제거
- 리뷰어가 보는 에러 다이얼로그가 사람이 읽을 수 있는 짧은 문구가 되도록

**B-5. 의존성 갱신**
- `flutter pub upgrade --major-versions` 후 iOS 26 호환성 검증
- 문제되는 패키지: `google_sign_in`, `sign_in_with_apple`, `flutter_naver_login`, `purchases_flutter`

#### Phase C. 검증
- [ ] iPad Pro 11" 시뮬레이터에서 Google/Apple/이메일 로그인 모두 성공
- [ ] iPhone 17 Pro Max 시뮬레이터에서 동일 검증
- [ ] 로그인 후 추가 정보 입력 화면까지 진행되는지 확인
- [ ] 자동 로그인 (앱 재시작) 동작 확인
- [ ] 화면 회전(가로/세로) 시 레이아웃 깨짐 없음
- [ ] TestFlight 빌드로 실기기 검증 영상 1개 확보

---

## 2. 거절 #2 — Guideline 2.1(b): IAP 에러

### 2.1 Apple 지적
> The In-App Purchase products in the app exhibited one or more bugs which create a poor user experience.
> Specifically, **an error message appeared when we attempted to purchase any in-app purchase item**.

### 2.2 의심되는 원인

#### A. RevenueCat Offerings 미로드
`lib/core/subscription/subscription_service.dart:194`
```
_lastError = 'Offerings에 current가 없습니다. RevenueCat 대시보드에서 ...'
```
- App Store Connect 에서 IAP 상품이 “Ready to Submit” 상태가 아니면 sandbox 에서 로드 안 됨
- RevenueCat 대시보드의 Offering 에 `swwim_month`, `swwim_years` 가 current 로 매핑되어 있어야 함

#### B. Paid Apps Agreement 미체결
- Apple 거절 메시지가 명시: *“the Account Holder must also accept the Paid Apps Agreement”*
- App Store Connect → Business → Agreements, Tax, and Banking 에서 Paid Apps 가 active 인지 확인 필수

#### C. 상품 메타데이터 누락
- IAP 상품 현지화(Display Name, Description) 가 한국어/영어 모두 채워져야 함
- `RESUBMISSION_CHECKLIST.md` 에 상품 메타데이터 정리되어 있음 — 실제 등록 상태 재확인

#### D. Sandbox 테스터 / 환경
- 리뷰어가 sandbox 환경에서 테스트할 때 `purchasesErrorCode` 가 `productNotAvailableForPurchase`, `storeProblemError`, `productAlreadyPurchased` 등으로 떨어질 수 있음
- 리뷰어 단말에 이미 sandbox 구독 이력이 있으면 “구매됨” 처리되며 에러 표시될 수 있음 → `restorePurchases` 안내 필요

#### E. 구매 실패 메시지 자체가 raw 노출
`subscription_service.dart:252`
```dart
_lastError = '구매 오류: ${errorCode.name} - ${e.message}';
```
- 사용자(=리뷰어)에게 `STORE_PROBLEM_ERROR - The operation couldn't be completed` 같은 raw 메시지 노출 → 거절 사유 가중 가능

#### F. 구매 버튼이 아무 응답 없음 / 로딩 멈춤
- `Purchases.purchasePackage` 응답 후 UI 갱신 누락
- 결제 시트가 떴다가 닫힌 후 결과 분기 누락

### 2.3 조치 계획

#### Phase A. 메타데이터 / 계정 점검 (App Store Connect)
- [ ] **Paid Apps Agreement** active 상태 확인 (Account Holder 로그인 필요)
- [ ] **Tax / Banking** 정보 완료
- [ ] **swwim_month, swwim_years** 상품 상태가 “Ready to Submit” 인지 확인
- [ ] 한국어 / 영어 현지화 (display name, description) 채움 (`RESUBMISSION_CHECKLIST.md` 기준)
- [ ] RevenueCat 대시보드에서 두 상품이 같은 Offering 에 매핑되어 current 로 설정되어 있는지 확인

#### Phase B. 코드 수정 후보

**B-1. 구매 에러 메시지 사용자 친화적으로**
`subscription_service.dart` 의 `purchase()` catch:
- `purchaseCancelledError` → 메시지 표시 안 함 (현재 OK)
- `productNotAvailableForPurchase` → `현재 구매할 수 없는 상품입니다. 잠시 후 다시 시도해 주세요.`
- `productAlreadyPurchasedError` → `이미 구매한 상품입니다. ‘구매 복원’을 눌러주세요.`
- `storeProblemError` / `networkError` → `App Store에 연결할 수 없습니다. 네트워크를 확인해 주세요.`
- 그 외 → `구매에 실패했습니다. 잠시 후 다시 시도해 주세요.`
- raw `e.message`, `errorCode.name` UI 노출 제거 (디버그 로그에는 유지)

**B-2. Offerings 미로드 시 안내 + 재시도**
- `currentOffering == null` 일 때 결제 화면을 띄우지 않거나, “상품 정보를 불러오는 중” 로딩 + “다시 시도” 버튼

**B-3. ‘구매 복원’ 버튼 기능 점검**
- iOS는 “Restore Purchases” 버튼 노출이 거의 의무 — `restorePurchases()` 호출 후 사용자에게 결과 토스트 표시

**B-4. 구매 직후 entitlement 재확인**
- 결제 성공 후 `customerInfo.entitlements.active` 에 `entitlementPremium` 이 즉시 들어오는지 확인
- 백엔드 isPremium 캐시(`subscription_service.dart` 의 backend cache)와 RevenueCat 상태 동기화 흐름 점검

#### Phase C. 검증
- [ ] Sandbox 테스터 계정으로 월간/연간 각각 구매 성공
- [ ] 의도적으로 취소 → 에러 메시지 안 뜸
- [ ] 네트워크 차단 후 구매 시도 → 사용자 친화적 메시지
- [ ] ‘구매 복원’ 동작 확인
- [ ] 화면 녹화 1개 확보 (월간 / 연간 각 1회 + 복원 1회)

---

## 3. 거절 #3 — Guideline 3.1.2(c): EULA 링크 누락

### 3.1 Apple 지적
> A functional link to the Terms of Use (EULA). If you are using the standard Apple Terms of Use (EULA), include a link to the Terms of Use in the **App Description**. If you are using a custom EULA, add it in App Store Connect.

### 3.2 현재 상태
- 앱 내부에 `lib/common/legal/terms_screen.dart` 가 존재 (이용약관 화면)
- App Store Connect 의 **App Description** 또는 **EULA** 필드에 외부 링크가 들어가 있는지 미확인

### 3.3 조치 계획

#### 옵션 A. Apple 표준 EULA 사용
- App Store Connect → App Information → **App Description** 본문 끝에 다음 링크 명시:
  ```
  Terms of Use (EULA): https://www.apple.com/legal/internet-services/itunes/dev/stdeula/
  ```
- 권장 (가장 단순, 추가 호스팅 불필요)

#### 옵션 B. 커스텀 EULA 사용
- 자체 EULA 텍스트를 작성 후 App Store Connect → App Information → **License Agreement (EULA)** 필드에 등록
- 외부에 호스팅된 URL 도 함께 두는 것이 안전 (`https://swwim.app/terms` 등)
- `terms_screen.dart` 의 본문과 일치시켜야 함 (앱 내부 ↔ 스토어 메타데이터 정합성)

### 3.4 동시 점검 (3.1.2 전체 요구사항)
앱 메타데이터 / 앱 화면 모두에 다음이 모두 노출되어야 함:
- [ ] 자동갱신 구독 제목 (`스윔 프리미엄 멤버십 (월간)` 등)
- [ ] 구독 기간 (월간 / 연간)
- [ ] 구독 가격 (단위 가격 포함)
- [ ] 개인정보처리방침 functional link (App Store Connect → Privacy Policy 필드)
- [ ] EULA functional link (App Description 또는 EULA 필드)
- [ ] 앱 내부 결제 화면에서도 약관 / 개인정보 / 가격 노출

---

## 4. 결정 필요 항목

- [ ] **거절 #1 로그인 오류 진짜 원인** — Phase A 재현 후 결정. 가능하면 리뷰어가 어느 단계에서 막혔는지 Apple 에 회신으로 추가 정보 요청
- [ ] **거절 #2 메시지 정책** — RevenueCat 에러 코드별 사용자 메시지 (위 B-1 안 그대로 갈지)
- [ ] **거절 #3 EULA 옵션** — A안(표준 EULA 링크) vs B안(커스텀 EULA). A안 권장
- [ ] **TestFlight 검증 단말** — 실제 iPad 가 없으면 시뮬레이터로 갈지, 외주 검증을 쓸지

---

## 5. 작업 우선순위 / 일정

| 순서 | 항목 | 예상 소요 |
| --- | --- | --- |
| 1 | 거절 #3 EULA 링크 추가 (App Store Connect 설정) | 30분 |
| 2 | 거절 #2 IAP 메타데이터 / Paid Apps Agreement 점검 | 1시간 |
| 3 | 거절 #2 코드 측 에러 메시지 정리 + 복원 버튼 점검 | 반나절 |
| 4 | 거절 #1 로그인 오류 재현 (시뮬레이터 + 시간 되면 실기기) | 반나절 |
| 5 | 거절 #1 원인별 수정 (iPad 레이아웃 / Apple 로그인 anchor / 의존성 / 에러 메시지) | 1~2일 |
| 6 | 전 시나리오 회귀 검증 + 화면 녹화 확보 | 반나절 |
| 7 | App Store Connect 메타데이터 최종 확인 후 1.0.4 (43) 빌드 제출 | 30분 |

---

## 6. 재제출 응답 초안 (Apple 회신용)

> Hello App Review team,
>
> Thank you for the detailed feedback. We have addressed all three issues for the next build:
>
> **2.1(a) Log in error:**
> - Verified login flow on iPad Pro 11" (M4) and iPhone 17 Pro Max with iPadOS/iOS 26.4.
> - Fixed iPad layout/presentation issues for the social sign-in flow.
> - Cleaned up error messages so users see a short, human-readable message.
>
> **2.1(b) IAP error:**
> - Confirmed Paid Apps Agreement is active.
> - Verified product configurations and metadata for `swwim_month` and `swwim_years` in App Store Connect.
> - Improved purchase error handling: cancellations no longer show errors, and other failures display a friendly message with a Restore Purchases option.
>
> **3.1.2(c) EULA link:**
> - Added a functional Terms of Use (EULA) link to the App Description / EULA field.
>
> A new build (1.0.4 / 43) is attached. Please let us know if any further details are needed.

---

## 7. 참고 파일 인덱스

코드:
- `lib/features/login/presentation/screens/login_sc.dart` (iPad 레이아웃 점검)
- `lib/core/auth/app_auth_service.dart` (Apple/Google/Naver/Email 로그인)
- `lib/features/login/data/repositories/auth_repository.dart` (에러 메시지)
- `lib/services/notification_service.dart` (APNs MethodChannel)
- `lib/core/subscription/subscription_service.dart` (RevenueCat 구매)
- `ios/Runner/Info.plist`, `ios/Runner/Runner.entitlements`

문서:
- `APP_REVIEW_REJECTION_GUIDE.md` (이전 거절 가이드)
- `RESUBMISSION_CHECKLIST.md` (메타데이터 체크리스트)
- `AUTH_ERROR_MESSAGE_CLEANUP.md` (로그인 에러 메시지 정리 기획)
- `NOTIFICATION_FEATURE_SPEC.md` (알림 전체 기획)
