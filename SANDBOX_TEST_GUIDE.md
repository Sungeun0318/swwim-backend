# iOS Sandbox 결제 테스트 가이드

> 목적: RevenueCat + Apple Sandbox 환경에서 실제 결제 흐름을 검증
> 사전 조건: App Store Connect IAP 상품이 "Ready to Submit" 상태

---

## ✅ 사전 체크리스트 — 시작 전 확인

| # | 항목 | 확인 방법 |
|---|---|---|
| 1 | **Paid Apps Agreement Active** | App Store Connect → Business → Agreements |
| 2 | **IAP 상품 "Ready to Submit"** | App Store Connect → 앱 → Subscriptions, `swwim_month` / `swwim_years` 모두 ✅ |
| 3 | **RevenueCat 상품 Active** | RevenueCat → Products, `DEVELOPER_ACTION_NEEDED` 없음 |
| 4 | **App Store Server Notifications URL 등록** | App Store Connect → 앱 → App Information → Server Notifications |
| 5 | **Sandbox 테스터 계정 1개 이상** | App Store Connect → Users and Access → Sandbox Testers (✅ 사용자 이미 생성함) |
| 6 | **iOS 디바이스 (실기기) 준비** | 시뮬레이터에서는 sandbox IAP 결제 안 됨 |
| 7 | **Xcode 설치 + Signing Team 설정** | `DEVELOPMENT_TEAM = X9W4A9RH66` 이미 설정됨 |

> ⚠️ **시뮬레이터로는 결제 테스트 불가**. RevenueCat sandbox는 실기기 + Sandbox Apple ID 로그인이 필수.

---

## 🚀 테스트 단계

### Step 1. 디바이스에 Sandbox 계정 등록

1. iPhone → **설정** → **App Store**
2. 화면 맨 아래로 스크롤 → **Sandbox 계정** → **로그인**
3. App Store Connect에서 만든 sandbox tester 이메일 입력
4. ⚠️ 일반 Apple ID는 **로그아웃하지 말 것**. Sandbox 계정은 별도 영역에 저장됨

### Step 2. Xcode로 디바이스에 빌드

```bash
cd /Users/sungeun/Developer/flutter/swwim
flutter clean
flutter pub get
cd ios && pod install && cd ..

# USB 또는 Wi-Fi로 디바이스 연결 후
flutter devices  # 디바이스 ID 확인
flutter run -d <device-id> --debug
```

또는 Xcode에서 직접:
```bash
open ios/Runner.xcworkspace
# 상단에서 디바이스 선택 → ▶️ 실행
```

> 💡 **TestFlight나 App Store 빌드는 sandbox 결제용으로 부적합**. 반드시 Xcode에서 직접 디바이스로 빌드.

### Step 3. 디바이스 콘솔 로그 확인 준비

**방법 A. Flutter 로그 (간편)**
- `flutter run`이 실행 중인 터미널에 실시간 출력됨
- `[구독]` 로그 검색

**방법 B. Console.app (상세)**
- macOS 기본 앱 → 디바이스 선택 → 프로세스 필터 `Runner`
- RevenueCat SDK 내부 로그까지 모두 보임 (`Purchases.setLogLevel(LogLevel.debug)` 적용됨)

### Step 4. 앱에서 결제 흐름 테스트

1. 앱 실행 → **로그인** (백엔드 user 생성 필요)
2. 로그에서 다음 확인:
   ```
   [구독] RevenueCat 유저 연결: <user.uid>
   [구독] 초기화 완료
     - isPremium: false
     - platform: iOS (Apple)
   ```
3. **More → 구독** (또는 결제 화면 진입 경로)
4. 패키지 로드 로그:
   ```
   [구독] Offerings 조회:
     - all offerings: [default]
     - current offering id: default
     - current packages: 2개
       · $rc_monthly (monthly) — swwim_month — ₩18,000
       · $rc_annual (annual) — swwim_years — ₩150,000
   ```
   - **packages: 0개** 또는 **current offering id: null**이면 RevenueCat 대시보드에서 current offering 미설정 → 대시보드에서 default offering을 current로 마크
5. **지금 구독하기** 클릭
6. iOS sandbox 결제 시트 표시 → Sandbox Apple ID 로그인 → 구매
7. 성공 로그:
   ```
   [구독] 구매 시도: $rc_monthly (swwim_month)
   [구독] 구매 결과: isPremium=true
     - productIdentifier: swwim_month
     - expirationDate: 2026-04-30T...
     - isSandbox: true
   [구독] CustomerInfo 변경:
     - isPremium: true
     - active entitlements: [premium]
   ```

### Step 5. RevenueCat 대시보드 확인

1. RevenueCat → **Customers** → 해당 user.uid 검색
2. 다음 항목 확인:
   - **Subscriber Attributes**에 user.uid 매칭
   - **Active Entitlements** → `premium` 표시
   - **Purchase History** → 방금 결제 기록
   - **Environment** = `Sandbox`

### Step 6. Backend Webhook 수신 확인

EC2 서버 로그 확인:
```bash
ssh -i <key> ec2-user@<ec2-ip>
sudo journalctl -u swwim-api -f | grep -i webhook
```

기대 로그:
```
[Webhook] RevenueCat 이벤트 수신: type=INITIAL_PURCHASE, env=SANDBOX, store=APP_STORE, productId=swwim_month, userId=<user.uid>, eventId=<uuid>
[Webhook] 🧪 SANDBOX 환경 이벤트 처리 중
[Webhook] ✅ premium 부여: userId=<user.uid>, productId=swwim_month, expiresAt=..., env=SANDBOX, entitlement_ids=[premium]
```

**Webhook이 안 들어오면:**
1. RevenueCat → Project Settings → Integrations → Webhooks → URL 확인
   - URL이 백엔드 주소 + `/api/v1/webhooks/revenuecat` 인지
   - Authorization Header에 `Bearer <key>` 설정했다면, backend `application.properties`의 `REVENUECAT_WEBHOOK_AUTH_KEY` 환경변수와 일치하는지
2. RevenueCat 대시보드 → Webhooks → "Send Test Event" 버튼으로 즉시 테스트
   - 백엔드 로그에 `[Webhook] ✅ TEST 이벤트 수신 — webhook 연결 정상` 보이면 OK

### Step 7. 갱신/만료 시나리오 테스트

Apple Sandbox는 **시간을 단축**해서 자동갱신을 시뮬레이션:

| 실제 구독 | Sandbox 단축 |
|---|---|
| 1개월 | 5분 |
| 2개월 | 10분 |
| 3개월 | 15분 |
| 6개월 | 30분 |
| 1년 | 1시간 |

**갱신 6회 후 자동 정지** (sandbox 한도). 그 시점부터 EXPIRATION 이벤트 발생.

**테스트 방법:**
1. 월간 구독 후 5분 대기 → RENEWAL 이벤트 확인
2. iPhone 설정 → Apple ID → Sandbox Account → Subscriptions → 구독 취소
3. 만료 시간 후 EXPIRATION 이벤트 확인

### Step 8. 차단 기능 테스트 (Apple 거절 사유 #1)

다른 sandbox 계정으로 게시글 작성한 후, 메인 계정에서:

| 시나리오 | 확인 |
|---|---|
| 게시글 더보기 → 차단 | ✅ 즉시 피드에서 게시글 사라짐 |
| **차단된 사용자** 메뉴 진입 | ✅ 방금 차단한 사용자 표시 |
| **차단된 사용자** 화면에서 차단 해제 | ✅ 즉시 목록에서 사라짐 |
| 메인 피드 복귀 | ✅ 차단 해제된 사용자 게시글 다시 보임 |
| 게시글 상세 진입 → 댓글 영역 | ✅ 차단된 사용자 댓글 안 보임 |

### Step 9. 화면 녹화 (재제출용)

iPhone에서:
1. **설정 → 제어 센터 → 화면 기록 추가**
2. 제어 센터에서 **● 화면 기록** 시작
3. 다음 흐름을 한 번에 녹화:
   - 결제 화면 진입 → 구독 안내 박스 노출
   - "지금 구독하기" → sandbox 결제 → 성공
   - 다른 사용자 차단 → 즉시 피드 갱신
   - 차단 해제 → 즉시 복귀
4. 녹화 종료 → 사진 앱에서 확인

이 동영상을 App Review 답변에 첨부하면 통과율 ↑.

---

## 🧯 문제 해결 (Troubleshooting)

### "Cannot connect to iTunes Store" 에러
- iPhone 설정 → App Store → Sandbox 계정 로그인 안 된 상태
- 또는 디바이스가 인터넷 연결 안 된 상태

### "There is no record of this app on the App Store"
- App Store Connect의 Bundle ID와 Xcode의 PRODUCT_BUNDLE_IDENTIFIER 불일치
- `com.zalmuk.swim` 으로 모두 통일되어 있는지 확인

### `current packages: 0개` 또는 `current offering id: null`
- RevenueCat → Offerings → default offering이 "current"로 마크되어 있어야 함
- 패키지에 상품이 매핑되어 있어야 함

### "Product is not available" / `productNotAvailableForPurchaseError`
- App Store Connect 상품 상태가 아직 "Ready to Submit" 아님
- 또는 업로드 직후 30분~1시간 propagation 대기 필요

### Webhook 수신 안 됨
- RevenueCat 대시보드 → Integrations → Webhooks → URL 정확한지 확인
- Authorization 헤더 사용 시 backend 환경변수 일치 확인
- 방화벽/Security Group에서 RevenueCat IP 차단 안 했는지 확인
- "Send Test Event"로 격리 테스트

### isSandbox=false로 들어옴
- 실수로 production Apple ID로 로그인한 경우
- 디바이스 → 설정 → App Store → Sandbox 계정 재로그인

### 같은 Sandbox 계정 재사용 시 "이미 구독 중" 에러
- Sandbox 계정 → 설정 → Apple ID → Subscriptions → 기존 구독 취소
- 또는 새 sandbox tester 계정 생성

---

## 📝 테스트 결과 기록 양식

테스트 진행하면서 다음 양식으로 결과를 기록하면 재제출 시 활용 가능:

```
=== Sandbox 결제 테스트 결과 ===
일시: 2026-04-30
디바이스: iPhone 17 Pro Max, iOS 26.4.1
Sandbox 계정: <테스터 이메일>

[ ] Step 1. Sandbox 계정 등록 — OK / NG
[ ] Step 2. Xcode 빌드 — OK / NG
[ ] Step 3. 앱 실행 + 로그인 — OK / NG
[ ] Step 4. Offerings 로드 — packages: ___개
[ ] Step 5. 월간 구독 결제 — OK / NG
[ ] Step 6. 연간 구독 결제 — OK / NG
[ ] Step 7. RevenueCat Customer 확인 — OK / NG
[ ] Step 8. Backend Webhook INITIAL_PURCHASE 수신 — OK / NG
[ ] Step 9. premium entitlement DB 반영 — OK / NG
[ ] Step 10. 5분 대기 후 RENEWAL 이벤트 — OK / NG
[ ] Step 11. 구독 취소 → EXPIRATION 이벤트 — OK / NG
[ ] Step 12. 차단/해제 즉시 반영 — OK / NG
[ ] Step 13. 화면녹화 완료 — OK / NG

특이사항:
- ...
```

---

## ⏭ 다음 단계

테스트 모두 통과 후:
1. **빌드 번호 증가**: `pubspec.yaml`의 `version: 1.0.3+41` → `1.0.3+42`
2. **TestFlight 빌드 업로드**: Xcode → Product → Archive → Distribute
3. **App Review 답변 작성**: `APP_REVIEW_REJECTION_GUIDE.md`의 영문 답변 템플릿 활용
4. **Submit for Review**
