# Apple App Review 거절 대응 가이드

> Submission ID: `ee8f38e4-2ec2-4116-a936-90b8f689c275`
> Review date: 2026-04-27
> Version: 1.0.3 (41)
> Review Device: iPhone 17 Pro Max, iOS 26.4.1

---

## 📋 거절 사유 3가지 요약

| # | Guideline | 영역 | 작업 위치 |
|---|---|---|---|
| 1 | **2.1(a)** App Completeness | 차단 기능 버그 | Flutter 코드 |
| 2 | **2.1(b)** App Completeness | IAP 구매 시 에러 발생 | App Store Connect + 코드 |
| 3 | **3.1.2(c)** Subscriptions | 구독 정보 / EULA 링크 누락 | App Store Connect + 코드 |

---

## 🔴 거절 1. 차단(Block) 기능 버그 — Guideline 2.1(a)

### Apple 지적 내용
> The app was unresponsive when we tapped on 차단 button and we were still able to see the blocked user's contents on the feed. The 차단된 사용자 page also did not update after we blocked a user.

### 원인 분석

**파일: `swwim/lib/common/community/services/block_service.dart`**

`BlockService`가 일반 싱글톤이고 **`ChangeNotifier`를 사용하지 않음**. 그래서 차단해도 위젯이 다시 빌드되지 않음.

```dart
// 현재 (문제)
class BlockService {
  Set<String> _blockedUserIds = {};
  // ❌ ChangeNotifier 아님 → 변경 알림 불가
}
```

**호출 측 문제:**
- `lib/common/community/screens/post_list.dart:684` — `_blockService.blockedUserIds`를 build에서 참조하지만 listener 없음 → 차단 후 setState 안 됨
- `lib/common/community/screens/community_screen.dart:238` — 동일 문제
- `lib/common/community/screens/blocked_users_screen.dart:19` — `initState`에서 한 번만 Future 로드, 차단/해제 후 자동 갱신 없음

### 해결 방법

#### ✅ Step 1. `BlockService`를 `ChangeNotifier`로 전환

`block_service.dart` 수정:

```dart
import 'package:flutter/foundation.dart';

class BlockService extends ChangeNotifier {  // ← extends ChangeNotifier
  // ... 기존 필드 그대로

  Future<bool> blockUser({...}) async {
    // ... 기존 로직
    if (success) {
      _blockedUserIds.add(userId);
      notifyListeners();  // ← 추가
    }
    return success;
  }

  Future<bool> unblockUser(String userId) async {
    // ... 기존 로직
    if (success) {
      _blockedUserIds.remove(userId);
      notifyListeners();  // ← 추가
    }
    return success;
  }

  Future<void> loadBlockedUsers() async {
    // ❌ 캐시 로딩 강제 갱신 옵션 추가
    final uid = _auth.currentUser?.uid;
    if (uid == null) return;
    try {
      final blockedUsers = await _communityApi.getBlockedUsers();
      _blockedUserIds = blockedUsers.map(...).toSet();
      _isCacheLoaded = true;
      notifyListeners();  // ← 추가
    } catch (e) { ... }
  }
}
```

#### ✅ Step 2. 차단 다이얼로그/시트에서 즉시 새로고침 보장

`block_user_dialog.dart`의 `_blockUser()` 메서드에서 차단 성공 후 명시적으로 알림:

```dart
Future<void> _blockUser() async {
  setState(() => _isLoading = true);
  final success = await _blockService.blockUser(...);
  // BlockService가 ChangeNotifier가 되었으므로 자동으로 listener에게 알림
  if (mounted) Navigator.pop(context, success);
}
```

#### ✅ Step 3. `post_list.dart` / `community_screen.dart`에서 listener 등록

```dart
@override
void initState() {
  super.initState();
  _blockService.loadBlockedUsers();
  _blockService.addListener(_onBlockChanged);  // ← 추가
}

@override
void dispose() {
  _blockService.removeListener(_onBlockChanged);  // ← 추가
  super.dispose();
}

void _onBlockChanged() {
  if (mounted) setState(() {});
}
```

#### ✅ Step 4. `BlockedUsersScreen` 자동 갱신

```dart
@override
void initState() {
  super.initState();
  _loadBlockedUsers();
  _blockService.addListener(_refreshList);  // ← 추가
}

@override
void dispose() {
  _blockService.removeListener(_refreshList);  // ← 추가
  super.dispose();
}
```

또한 `blocked_users_screen.dart:213`의 차단 해제 다이얼로그에서:

```dart
final success = await _blockService.unblockUser(userId);
if (success && mounted) {
  _refreshList();  // ← 강제 새로고침 (이미 있으면 OK)
}
```

#### ✅ Step 5. 게시글 상세 진입 시 차단 상태 재확인

`post_detail_screen.dart`에서도 차단된 사용자의 댓글이 보이지 않도록 동일하게 listener 등록.

---

## 🔴 거절 2. IAP 구매 에러 — Guideline 2.1(b)

### Apple 지적 내용
> The app still showed an error message when we tapped on 지금 구독하기.

### 원인

이건 RevenueCat 대시보드에서 본 **`DEVELOPER_ACTION_NEEDED`** 상태와 동일한 원인. App Store Connect에서 IAP 상품 메타데이터가 미완성이라 `Purchases.getOfferings()`이 빈 결과를 반환 → `_packages`가 비어서 결제 시도 시 에러.

### 해결 방법

**App Store Connect에서 작업** (코드 수정 X):

#### ✅ Step 1. Paid Apps Agreement 확인

1. [App Store Connect](https://appstoreconnect.apple.com) 로그인 (Account Holder 계정)
2. **Business** → **Agreements, Tax, and Banking**
3. **Paid Apps** 계약이 "Active" 상태인지 확인
   - "Action Required"면 모든 항목(은행 계좌, 세금 정보, 연락처) 완료
4. Active 안 되면 IAP 자체가 동작 안 함

#### ✅ Step 2. IAP 상품 메타데이터 완성

각 상품(`swwim_month`, `swwim_years`)을 클릭해서 빨간 점(●) 항목 모두 채우기:

| 항목 | 값 예시 |
|---|---|
| Reference Name | `Swwim Premium Monthly` / `Swwim Premium Yearly` |
| Product ID | `swwim_month` / `swwim_years` (이미 설정됨) |
| Subscription Duration | `1 Month` / `1 Year` |
| Subscription Group | 둘이 같은 그룹에 속해야 함 (예: `Swwim Premium`) |
| Price | ₩18,000 / ₩150,000 |
| Tax Category | `Subscription` |

#### ✅ Step 3. 현지화 (거절된 부분 재작성)

스크린샷에서 본 `swim 프리미엄 월간 구독` / `SWWIM 프리미엄 월간 구독으로 모든 기능을 이용하세요.` 가 **거절됨** 상태.

거절 이유는 보통 **Display Name과 Description이 너무 단순**하거나 **앱과 일관성 없음**.

**개선된 한국어 현지화 예시:**

| 필드 | 월간 (`swwim_month`) | 연간 (`swwim_years`) |
|---|---|---|
| Display Name | `스윔 프리미엄 (월간)` | `스윔 프리미엄 (연간)` |
| Description | `매월 자동 갱신되는 프리미엄 멤버십입니다. 커스텀 훈련 설정, 광고 제거, 음성 출발 신호 변경, 타종 타이머, 복수선수 모드 등 모든 프리미엄 기능을 이용할 수 있습니다.` | `1년 자동 갱신 프리미엄 멤버십. 월간 대비 33% 할인된 가격으로 커스텀 훈련, 광고 제거, 음성 출발 신호, 타종 타이머, 복수선수 모드 등 모든 프리미엄 기능을 이용할 수 있습니다.` |

**영어(en-US)도 추가 권장** — Apple 리뷰어가 영어로 검토할 수 있어서:

| 필드 | Monthly | Yearly |
|---|---|---|
| Display Name | `Swwim Premium (Monthly)` | `Swwim Premium (Yearly)` |
| Description | `Auto-renewable monthly Premium membership. Unlock custom training settings, ad-free experience, voice start signals, bell timer, and multi-swimmer mode.` | `Auto-renewable yearly Premium membership at 33% off. Includes all Premium features: custom training, ad-free, voice start signals, bell timer, multi-swimmer mode.` |

#### ✅ Step 4. Review Information

각 상품에 **Review Screenshot** 첨부 (필수). 1024×1024 또는 앱 화면 캡처. 결제 화면 (PaymentScreen) 스크린샷이 적합.

**Review Notes 예시:**
```
This is an auto-renewable subscription. To test:
1. Login with sandbox tester
2. Navigate to More → Subscription
3. Tap "지금 구독하기" button
```

#### ✅ Step 5. RevenueCat 동기화

App Store Connect 상품 상태가 모두 `Ready to Submit` 되면:
1. RevenueCat 대시보드 → **Products** → 해당 상품의 `...` → **Refresh from App Store Connect**
2. `DEVELOPER_ACTION_NEEDED` → `Active`로 바뀌어야 함

#### ✅ Step 6. App Store Server Notifications V2 설정

App Store Connect → 앱 → **App Information** → **App Store Server Notifications**:
- **Production URL**: RevenueCat 대시보드 → Project Settings → Apps → iOS 앱 → "App Store Server Notifications URL" 복사하여 입력
- **Sandbox URL**: 동일

이걸 안 하면 RevenueCat이 갱신/취소 이벤트를 받지 못해서 백엔드 webhook도 못 받음.

---

## 🔴 거절 3. 구독 정보 / EULA 누락 — Guideline 3.1.2(c)

### Apple 지적 내용

자동갱신 구독 앱의 필수 정보:
- ✅ Title of subscription (이미 있음: "프리미엄 가입")
- ✅ Length of subscription (이미 있음: "월"/"년")
- ✅ Price of subscription (이미 있음: ₩18,000/₩150,000)
- ⚠️ **Functional links to Privacy Policy and Terms of Use (EULA)**
  - 앱 내에 있긴 하지만 Apple이 부족하다고 판단
  - **App Store Connect 메타데이터에도 Privacy Policy URL이 등록되어 있어야 함**
  - **EULA는 App Description에 링크 또는 EULA 필드에 등록**

### 해결 방법

#### ✅ Step 1. App Store Connect — Privacy Policy URL 등록

App Store Connect → 앱 → **App Privacy** → **Privacy Policy URL** 입력
(예: `https://swwim.example.com/privacy`)

#### ✅ Step 2. App Store Connect — EULA 등록

두 가지 방법 중 선택:

**방법 A. 표준 Apple EULA 사용 (권장)**
- App Store Connect → 앱 → **App Description** 끝에 다음 텍스트 추가:

```
Terms of Use (EULA): https://www.apple.com/legal/internet-services/itunes/dev/stdeula/
```

**방법 B. 커스텀 EULA**
- App Store Connect → 앱 → **General** → **App Information** → **License Agreement (EULA)** 항목에 직접 작성/업로드

#### ✅ Step 3. PaymentScreen 구독 정보 명시 강화

현재 `payment_screen.dart`에 구독 정보가 있긴 한데, Apple이 더 명확하게 보여달라는 요구. 다음 텍스트를 결제 버튼 **위쪽**에 명시:

```dart
// payment_screen.dart 의 _buildFooter() 안, 구독 버튼 위에 추가
Container(
  margin: const EdgeInsets.only(bottom: 12),
  padding: const EdgeInsets.all(12),
  decoration: BoxDecoration(
    color: Colors.grey.shade50,
    borderRadius: BorderRadius.circular(8),
  ),
  child: Column(
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Text(
        '구독 안내',
        style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold,
                       color: Colors.grey.shade700),
      ),
      const SizedBox(height: 4),
      Text(
        '• ${_selectedPlan == "yearly" ? "연간" : "월간"} 자동갱신 구독: '
        '₩${selectedPlanData["price"]}/${selectedPlanData["period"]}\n'
        '• 결제는 Apple ID로 진행되며, 현재 구독 기간 종료 24시간 전에 '
        '자동으로 갱신됩니다.\n'
        '• iPhone 설정 > Apple ID > 구독에서 언제든 취소 가능합니다.',
        style: TextStyle(fontSize: 11, color: Colors.grey.shade600,
                        height: 1.5),
      ),
    ],
  ),
),
```

#### ✅ Step 4. PaymentScreen에 EULA 링크 명시

현재 푸터에 "서비스 이용약관" 링크는 있지만, EULA 표시도 명시:

```dart
// payment_screen.dart 푸터의 약관 부분 수정
Wrap(
  alignment: WrapAlignment.center,
  children: [
    Text('구독 시 ', style: ...),
    GestureDetector(
      onTap: () => Navigator.push(context, MaterialPageRoute(
        builder: (_) => const TermsScreen())),
      child: Text('이용약관(EULA)', style: ...),  // ← "서비스 이용약관" → "이용약관(EULA)"
    ),
    Text(' 및 ', style: ...),
    GestureDetector(
      onTap: () => Navigator.push(context, MaterialPageRoute(
        builder: (_) => const PrivacyScreen())),
      child: Text('개인정보처리방침', style: ...),
    ),
    Text('에 동의하게 됩니다', style: ...),
  ],
),
```

---

## 🛠 작업 순서 (권장)

```
1. App Store Connect 작업 (계정 보유자만 가능)
   ├─ Paid Apps Agreement Active 확인
   ├─ Privacy Policy URL 등록
   ├─ EULA 추가 (App Description 또는 EULA 필드)
   ├─ IAP 상품 메타데이터 완성 (월간/연간)
   ├─ 한국어/영어 현지화 재작성
   ├─ Review Screenshot 첨부
   ├─ App Store Server Notifications URL 등록 (RevenueCat URL)
   └─ 모든 상품 상태 "Ready to Submit" 확인

2. RevenueCat 대시보드
   └─ Refresh from App Store Connect → Active 확인

3. Flutter 코드 수정
   ├─ block_service.dart → ChangeNotifier 전환
   ├─ post_list.dart, community_screen.dart, blocked_users_screen.dart, post_detail_screen.dart → listener 등록
   ├─ payment_screen.dart → 자동갱신 구독 안내 텍스트 추가
   └─ payment_screen.dart → EULA 링크 명시 강화

4. Sandbox 결제 테스트
   ├─ iPhone 디바이스에서 Xcode 빌드
   ├─ 설정 → App Store → Sandbox Account 로그인
   ├─ 앱에서 "지금 구독하기" → 결제 흐름 끝까지
   ├─ RevenueCat 대시보드 Customers에서 entitlement 확인
   └─ Backend 로그에서 webhook 수신 확인

5. 차단 기능 테스트
   ├─ 다른 계정 게시글에서 "차단하기" → 즉시 피드에서 사라지는지
   ├─ 차단된 사용자 페이지 → 즉시 표시되는지
   ├─ 차단 해제 → 즉시 피드에 복귀하는지
   └─ 게시글 상세에서 차단된 사용자 댓글 안 보이는지

6. 재제출
   ├─ Build number 증가 (41 → 42)
   ├─ Version note에 수정사항 명시
   ├─ Reply to App Review에 화면녹화 첨부 권장
   │  - 차단 후 즉시 피드 갱신 보여주기
   │  - 결제 흐름 정상 동작 보여주기
   └─ Submit
```

---

## 📝 재제출 시 Apple에 보낼 답변 예시 (영문)

```
Hello App Review Team,

Thank you for the detailed feedback. We have addressed all three issues:

1. Guideline 2.1(a) — Block functionality bug:
   - Refactored BlockService to use ChangeNotifier pattern.
   - Feed and "차단된 사용자" page now refresh immediately upon block/unblock.
   - Tested on iPhone with sandbox account.

2. Guideline 2.1(b) — In-App Purchase error:
   - Completed all metadata for swwim_month and swwim_years products in App Store Connect.
   - Refreshed RevenueCat configuration; both products are now Active.
   - Verified successful purchase flow with sandbox testers.

3. Guideline 3.1.2(c) — Subscription information:
   - Added explicit auto-renewal disclosure on the purchase screen
     (subscription title, length, price, auto-renewal terms).
   - Made EULA link prominent on the purchase screen.
   - Added Privacy Policy URL and standard Apple EULA link in App Store Connect.

A screen recording demonstrating the fixes is attached.

Best regards,
WELL EATEN COMPANY
```

---

## ⚠️ 추가 체크 포인트

- [ ] `REVENUECAT_WEBHOOK_AUTH_KEY` 환경변수 설정 (보안)
- [ ] Backend `application.properties`의 `revenuecat.webhook.auth-key` ↔ RevenueCat 대시보드 webhook 설정의 `Authorization` 헤더 일치
- [ ] iPad에서도 정상 동작 확인 (Apple은 iPad Air 11-inch (M3)에서도 테스트함)
- [ ] iOS 26.4.1에서 테스트 (Apple 리뷰 환경)
- [ ] 빌드 번호 증가 잊지 말기 (41 → 42)
