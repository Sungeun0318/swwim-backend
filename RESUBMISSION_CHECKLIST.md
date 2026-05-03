# App Review 재제출 체크리스트

> 대상: Submission ID `ee8f38e4-2ec2-4116-a936-90b8f689c275` 거절분 재제출
> 코드 측 작업: 모두 완료 (Phase 1 + Phase 2)
> 남은 작업: 99% App Store Connect 메타데이터 + 화면 녹화 + 제출

---

## 1️⃣ App Store Connect — IAP 상품 현지화 (거절분 재작성)

### 한국어 (필수)

#### 월간 (`swwim_month`)
| 필드 | 입력값 | 자수 |
|---|---|---|
| 표시 이름 | `스윔 프리미엄 멤버십 (월간)` | 17자 |
| 설명 | `매월 자동갱신 멤버십. 커스텀 훈련, 광고 제거, 음성 신호 등 프리미엄 기능 이용.` | 41자 |

#### 연간 (`swwim_years`)
| 필드 | 입력값 | 자수 |
|---|---|---|
| 표시 이름 | `스윔 프리미엄 멤버십 (연간)` | 17자 |
| 설명 | `1년 자동갱신 멤버십 (월간 대비 33% 할인). 모든 프리미엄 기능 이용.` | 38자 |

### 영어 — en-US (강력 권장)

Apple 리뷰어는 영문 환경에서 검토. 영문 현지화 없으면 한국어를 영문으로 자동 번역해서 보는데, 자동 번역이 어색하면 거절 가능.

#### 월간 (`swwim_month`)
| 필드 | 입력값 |
|---|---|
| Display Name | `Swwim Premium (Monthly)` |
| Description | `Auto-renewing monthly Premium membership.` |

#### 연간 (`swwim_years`)
| 필드 | 입력값 |
|---|---|
| Display Name | `Swwim Premium Yearly (33% off)` |
| Description | `Auto-renewing yearly Premium with 33% off.` |

---

## 2️⃣ App Store Connect — 그 외 메타데이터

### 비즈니스 / 계약
- [ ] **Paid Apps Agreement** — Business → Agreements → "Active" 상태 확인
- [ ] 은행 계좌, 세금 정보, 연락처 정보 모두 입력 완료

### 앱 정보 (App Information)
- [ ] **Privacy Policy URL** 등록 (예: `https://swwim.example.com/privacy`)
- [ ] **EULA** 등록 — 두 가지 방법 중 택1:
  - 방법 A. App Description 끝에 추가:
    ```
    Terms of Use (EULA): https://www.apple.com/legal/internet-services/itunes/dev/stdeula/
    ```
  - 방법 B. App Information → License Agreement (EULA) 필드에 직접 입력

### IAP 상품 설정 (각 상품 클릭해서 항목별로)
- [ ] **Subscription Group** — `swwim_month` / `swwim_years` 둘이 같은 그룹에 속해야 함 (예: `Swwim Premium`)
- [ ] **Subscription Duration** — 각각 `1 Month` / `1 Year` 명시
- [ ] **Pricing** — 한국 가격 ₩18,000 / ₩150,000 입력 + Apple 권장 다른 지역 가격 자동 채움
- [ ] **Tax Category** — `Subscription` 선택
- [ ] **Family Sharing** — 사용 안 함이면 명시적으로 OFF (안 건드리면 일부 케이스에서 Action Needed)

### Review Information (각 상품마다)
- [ ] **Review Screenshot** 첨부 (1024×1024 또는 결제 화면 스크린샷)
  - 권장: PaymentScreen 캡처 (구독 안내 박스가 보이는 화면)
- [ ] **Review Notes** 작성:
  ```
  This is an auto-renewable Premium subscription.
  To test:
  1. Login with provided sandbox tester account
  2. Tap "Premium" badge in top-right of home screen
  3. Or navigate: More → 결제/구독
  4. Select plan → Tap "지금 구독하기"
  ```

### Server Notifications
- [ ] **App Store Server Notifications V2** Production URL 등록
  - URL은 RevenueCat 대시보드 → Project Settings → Apps → iOS → "App Store Server Notifications URL" 복사
- [ ] **Sandbox URL** 동일하게 등록

### 상태 확인
- [ ] 두 상품 모두 **"Ready to Submit"** 상태
- [ ] RevenueCat 대시보드 → Refresh from App Store Connect → 두 상품 모두 **Active**

---

## 3️⃣ RevenueCat 측 점검

- [ ] Project Settings → Apps → iOS 앱 클릭 시 빨간 점(❗) 없음
- [ ] **In-App Purchase Key (.p8)** 또는 **App-Specific Shared Secret** 등록 완료
- [ ] **App Store Connect API** 통합 완료
- [ ] Webhook URL이 백엔드로 향하는지 확인 (선택, 보안 강화 시)

---

## 4️⃣ Flutter 빌드 + TestFlight 업로드

### 버전 증가
- [ ] `swwim/pubspec.yaml` 의 `version: 1.0.3+41` → **`1.0.3+42`**

### 빌드
- [ ] `flutter clean && flutter pub get`
- [ ] `cd ios && pod install && cd ..`
- [ ] Xcode 열어서 Archive
  ```bash
  open swwim/ios/Runner.xcworkspace
  ```
- [ ] Xcode → Product → Archive
- [ ] Distribute App → App Store Connect → Upload

### TestFlight
- [ ] 업로드 완료 후 ~30분 대기 (processing)
- [ ] TestFlight 빌드 등장 확인
- [ ] Internal Testing 으로 본인 디바이스에 배포 (sandbox 테스트용)

---

## 5️⃣ Sandbox 테스트 (실기기)

`SANDBOX_TEST_GUIDE.md` 9단계 따라 진행. 핵심만:

- [ ] iPhone 설정 → App Store → Sandbox 계정 로그인 (이미 만든 tester)
- [ ] TestFlight 또는 Xcode 디바이스 빌드로 앱 실행
- [ ] 홈 화면 우측 상단에 "Premium" 골드 배지 보임 (비구독자)
- [ ] 누르면 → PaymentScreen → "지금 구독하기"
- [ ] Sandbox Apple ID 로그인 → 결제 → 성공
- [ ] 배지가 🟢 "Premium 회원" 으로 변경
- [ ] RevenueCat → Customers → entitlement 부여 확인
- [ ] EC2 로그에서 `[Webhook] ✅ premium 부여` 확인

```bash
ssh -i "/Users/sungeun/Developer/flutter/key파일/swwim-key.pem" \
  ec2-user@13.124.29.102 'tail -100 /home/ec2-user/app.log | grep Webhook'
```

---

## 6️⃣ 차단 기능 검증 (Apple 거절 사유 #1)

다른 sandbox 계정으로 게시글 작성한 뒤 메인 계정에서:
- [ ] 게시글 더보기 → "차단하기" → **즉시 피드에서 사라짐**
- [ ] More → 차단된 사용자 페이지 → 방금 차단한 사용자 표시
- [ ] "차단 해제" → 즉시 목록에서 사라지고 피드 복귀

---

## 7️⃣ 화면 녹화 (재제출 첨부용)

iPhone 설정 → 제어 센터 → 화면 기록 추가, 다음 흐름을 한 번에 녹화:

1. 홈 화면 → 우측 상단 "Premium" 배지 → PaymentScreen 진입
2. 구독 안내 박스 + EULA/Privacy 링크 보이는 모습
3. 플랜 선택 → "지금 구독하기" → Sandbox 결제 성공
4. 배지가 🟢 "Premium 회원" 으로 변경되는 모습
5. (별도) 다른 사용자 게시글 → 차단 → 즉시 피드 갱신

→ 영상은 iPhone 사진 앱에서 가져와 App Review 답변에 첨부.

---

## 8️⃣ App Review 답변 (Reply to App Review)

App Store Connect → 거절된 빌드 → "Reply to App Review" 또는 새 제출 시 Notes 영역.

영문 권장:

```
Hello App Review Team,

Thank you for the detailed feedback. We have addressed all three issues:

1. Guideline 2.1(a) — Block functionality bug:
   - Refactored BlockService to use ChangeNotifier pattern.
   - Feed and "차단된 사용자" page now refresh immediately upon block/unblock.
   - Tested on iPhone with sandbox account.

2. Guideline 2.1(b) — In-App Purchase error:
   - Completed all metadata for swwim_month and swwim_years products
     in App Store Connect (Korean + English localization).
   - Refreshed RevenueCat configuration; both products are now Active.
   - Verified successful purchase flow with sandbox tester.

3. Guideline 3.1.2(c) — Subscription information:
   - Added explicit auto-renewal disclosure on the purchase screen
     (subscription title, length, price, auto-renewal terms, cancellation method).
   - Made EULA and Privacy Policy links prominent on the purchase screen.
   - Added Privacy Policy URL and standard Apple EULA link
     in App Store Connect metadata.

A screen recording demonstrating all three fixes is attached.

For testing:
- Sandbox tester account: <sandbox tester email>
- Test flow:
  1. Launch app, login with provided account
  2. Tap "Premium" badge in top-right
  3. Select monthly or yearly plan
  4. Complete sandbox purchase
  5. Block functionality: tap "더보기" → "차단" on any post

Best regards,
WELL EATEN COMPANY
```

---

## 9️⃣ 제출 (Submit for Review)

- [ ] App Store Connect → 빌드 41 거절분 → "Submit for Review"
- [ ] 빌드 42 선택
- [ ] 제출 시:
  - Cryptography 질문: ITSAppUsesNonExemptEncryption 이미 false (Info.plist)
  - Export Compliance: 일반 HTTPS 만 사용 → "No"
  - Advertising Identifier: AdMob 사용 중 → "Yes" → "Serve advertisements within the app" 체크

---

## 🔟 제출 후 모니터링

- [ ] App Store Connect → 빌드 상태 "In Review" 진입 (보통 24시간 이내)
- [ ] EC2 로그 모니터링 — Apple 리뷰어가 결제 시도 시 Webhook 로그 확인
- [ ] 통과 시: "Ready for Distribution" 상태로 전환

---

## 📊 통과 가능성 체크 — 거절 사유 vs 해결책

| 거절 사유 | 코드 측 해결 | 메타데이터 해결 | 완료 |
|---|---|---|---|
| 2.1(a) 차단 버그 | ✅ ChangeNotifier 패턴 + 4개 화면 listener | - | 코드 ✅ |
| 2.1(b) IAP 에러 | - | 위 1️⃣ + 2️⃣ + 3️⃣ | 메타데이터 ⏳ |
| 3.1.2(c) 자동갱신 정보 | ✅ PaymentScreen 자동갱신 박스 + EULA 라벨 | 위 2️⃣ Privacy Policy URL + EULA | 양쪽 모두 ⏳ |

코드는 다 끝났고, **App Store Connect 메타데이터 작업이 95% 의 비중**.

---

## 🟢 추가 권장 작업 (출시 후 해도 됨)

1. **회원 탈퇴 전 활성 구독 경고 다이얼로그**
   - 사용자가 hard delete 후 RevenueCat 구독 자동 취소 안 되는 문제 방지
   - 1시간 작업, 다음 업데이트에서 추가
2. **백엔드 API 게이팅** — Spring Security `@PreAuthorize` 추가
3. **구독 만료 푸시 알림** — RevenueCat webhook + FCM 통합
4. **REVENUECAT_WEBHOOK_AUTH_KEY** 설정 (보안 강화)
5. **앱 내 관리자 GUI 화면** — 사용자 검색 + admin 토글

---

## ⚠️ 주의: Apple 자주 거절하는 패턴 — 미리 점검

- ✅ 광고된 기능 미구현: 타종 타이머 = 복수선수 모드로 통합됨, 실제 동작 함
- ✅ 데이터 수집 동의: Privacy Policy 명시 (메타데이터에 등록 후)
- ✅ 외부 결제 안내: 없음 (RevenueCat IAP만 사용)
- ✅ 회원 탈퇴 가능: hard delete 구현됨 (`UserService.deleteUser`)
- ✅ Sign in with Apple 옵션: `Runner.entitlements` 에 등록됨
- ✅ 위치 권한 안내: Info.plist 에 사유 명시됨
- ⚠️ 카메라 권한: 프로필 사진 업로드 기능 → 사유 명시됨 (`NSCameraUsageDescription`)

---

## 🎯 작업 순서 권장

```
Day 1 (App Store Connect 작업, 약 2-3시간)
  ├─ Paid Apps Agreement 활성 확인
  ├─ Privacy Policy URL 등록
  ├─ EULA 등록
  ├─ IAP 상품 메타데이터 (한국어 + 영어)
  ├─ Review Screenshot 첨부
  ├─ App Store Server Notifications URL 등록
  └─ 두 상품 "Ready to Submit" 확인

Day 1 또는 Day 2 (RevenueCat 동기화)
  └─ Refresh from App Store Connect → Active 확인

Day 2 (Flutter 빌드, 약 1시간)
  ├─ pubspec.yaml version +1
  ├─ flutter clean / pub get / pod install
  ├─ Xcode Archive → TestFlight
  └─ TestFlight 빌드 처리 대기

Day 2 또는 Day 3 (Sandbox 테스트, 약 1시간)
  ├─ TestFlight 빌드 설치
  ├─ Sandbox Apple ID로 결제 흐름 검증
  ├─ 차단 기능 검증
  └─ 화면 녹화

Day 3 (제출)
  ├─ App Review 답변 작성
  ├─ 화면 녹화 첨부
  └─ Submit for Review

Day 4-5 (대기)
  └─ Apple 리뷰 결과 대기 (보통 24-48시간)
```

---

## 💬 질문/이슈 시 참고할 다른 가이드

- `APP_REVIEW_REJECTION_GUIDE.md` — 원본 거절 사유 분석 + 영문 답변
- `SANDBOX_TEST_GUIDE.md` — sandbox 결제 테스트 9단계 + Troubleshooting
- `PREMIUM_GATING_PLAN.md` — Phase 1 게이팅 결과 (참고용)
- `PHASE2_PLAN.md` — Phase 2 작업 명세 (코덱스 완료)
