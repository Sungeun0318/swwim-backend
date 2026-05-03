# Phase 2 — 추가 게이팅 / UX 개선 계획

> 작성일: 2026-04-30
> 전제: Phase 1 (커스텀 훈련 게이팅, GitHubAppBar 3-state 배지, admin/구독자 처리) 완료된 상태에서 추가로 손볼 항목

---

## 📋 작업 목록 요약

| # | 항목 | 영역 | 우선순위 |
|---|---|---|---|
| 1 | 설정 → 결제/구독: 구독자/관리자에게 다른 UI 표시 | UX | 높 |
| 2 | 커뮤니티 공유 훈련 클릭 시 확인 다이얼로그 후 타이머 진행 | UX | 높 |
| 3 | 캘린더 훈련 시작에 프리미엄 게이팅 | 기능 차단 | 높 |

---

## 🔴 작업 1. 설정 → 결제/구독 — 상태별 분기

### 현재 동작 (버그)
**위치**: `lib/common/more/more_screen.dart:555-566`

```dart
_buildMenuItem(
  icon: Icons.credit_card,
  label: '결제 / 구독',
  onTap: () {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (ctx) => const PaymentScreen()),
    );
  },
),
```

→ 무조건 `PaymentScreen` 으로 이동 → 이미 구독자인데도 "지금 구독하기" 버튼이 떠 있어서 사용자 혼란.

### 목표 동작

| 사용자 상태 | 메뉴 라벨 | 누르면 동작 |
|---|---|---|
| 비구독자 | "결제 / 구독" | `PaymentScreen` (현재와 동일) |
| 구독자 | "구독 중 (관리)" | 구독 정보 다이얼로그 — `GitHubAppBar`의 `_showPremiumInfoDialog()` 와 동일 (만료일 / 자동갱신 / 구독 관리) |
| 관리자 | "관리자 권한" | 관리자 안내 다이얼로그 — `GitHubAppBar`의 `_showAdminInfoDialog()` 와 동일 |

### 수정 방안

**옵션 A. 메뉴 항목 자체를 상태별로 분기 (권장)**

`more_screen.dart`에서 `SubscriptionService.instance.isAdmin / isPremium` 체크 후 메뉴 항목 분기:

```dart
// 결제/구독 관련 메뉴 — 상태별 분기
if (SubscriptionService.instance.isAdmin) {
  _buildMenuItem(
    icon: Icons.shield_moon,
    label: '관리자 권한',
    iconColor: const Color(0xFF7C3AED),
    onTap: () => _showAdminInfoDialog(),
  )
} else if (SubscriptionService.instance.isPremium) {
  _buildMenuItem(
    icon: Icons.verified,
    label: '구독 중 (관리)',
    iconColor: const Color(0xFF059669),
    onTap: () => _showPremiumInfoDialog(),
  )
} else {
  _buildMenuItem(
    icon: Icons.credit_card,
    label: '결제 / 구독',
    onTap: () => Navigator.push(context,
        MaterialPageRoute(builder: (ctx) => const PaymentScreen())),
  )
}
```

**옵션 B. 다이얼로그 헬퍼 통합**

`GitHubAppBar`의 `_showPremiumInfoDialog()` / `_showAdminInfoDialog()` 를 별도 파일 (`lib/core/subscription/subscription_dialogs.dart`) 로 추출해서 재사용. 더 깔끔.

→ **옵션 B 권장.** `subscription_dialogs.dart` 새로 만들고, GitHubAppBar 와 more_screen 둘 다에서 호출.

### 변경 파일
- `lib/common/more/more_screen.dart` — 결제/구독 메뉴 분기 로직
- `lib/core/subscription/subscription_dialogs.dart` (신규) — 다이얼로그 공통 헬퍼
- `lib/common/widgets/github_app_bar.dart` — 다이얼로그 호출만 남기고 본체 제거

### State 변경 listen
`SubscriptionService` 가 `ChangeNotifier` 이므로, `_MoreScreenState` 에서:
```dart
@override
void initState() {
  super.initState();
  SubscriptionService.instance.addListener(_onSubChanged);
  // ...
}

@override
void dispose() {
  SubscriptionService.instance.removeListener(_onSubChanged);
  super.dispose();
}

void _onSubChanged() => mounted ? setState(() {}) : null;
```

---

## 🔴 작업 2. 커뮤니티 공유 훈련 — 확인 다이얼로그 후 타이머 진행

### 사용자 의도
> "결제 전인 사람들은 커뮤니티에서 다른 사용자가 공유한 훈련을 눌렀을 때
> 자동으로 훈련 진행하겠습니까? 문구가 뜨고 바로 타이머로 갈 수 있게끔
> 그 타이머는 당연히 그 커뮤니티 공유된 훈련 내용으로 작동하면됨"

→ 커뮤니티 공유 훈련은 **무료 사용자도 사용 가능 (게이팅 없음)**, 단 진입 전 의도 확인 다이얼로그 추가.

### 현재 동작
**위치**: `lib/common/community/screens/post_detail_screen.dart:876-903`

```dart
if (shareType == 'training' && trainingData != null) ...[
  GestureDetector(
    onTap: () {
      final sessionId = (trainingData?['sessionId'] ?? '').toString();
      Navigator.push(context, MaterialPageRoute(
        builder: (_) => TGTimerScreenModular(
          sessionId: sessionId.isNotEmpty ? sessionId : 'temp',
          fallbackData: TrainingSession.fromMap(
            trainingData ?? {},
            sessionId.isNotEmpty ? sessionId : 'temp',
          ),
        ),
      ));
    },
    child: _TrainingSharedCard(trainingData: trainingData),
  ),
]
```

→ 클릭 즉시 타이머로 이동. 의도 확인 없음.

### 목표 동작

1. 사용자가 공유된 훈련 카드 탭
2. 다이얼로그 표시:
   ```
   "이 훈련을 진행하시겠습니까?
    훈련 이름: <훈련명>
    총 거리: <distance>
    예상 시간: <time>"
   [취소] [훈련 진행]
   ```
3. "훈련 진행" 누르면 → `TGTimerScreenModular` 로 이동 (현재 동작과 동일)
4. "취소" 누르면 → 닫고 끝

### 수정 방안

`post_detail_screen.dart` 의 `onTap` 핸들러를 다이얼로그 분기로 교체:

```dart
onTap: () async {
  final confirmed = await showDialog<bool>(
    context: context,
    builder: (ctx) => AlertDialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      title: Row(
        children: const [
          Icon(Icons.pool, color: Color(0xFF0B63A7)),
          SizedBox(width: 8),
          Expanded(child: Text('훈련 진행')),
        ],
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('이 훈련을 진행하시겠습니까?'),
          const SizedBox(height: 12),
          // 훈련 정보 (이름/거리/시간)
          ...
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(ctx, false),
          child: const Text('취소'),
        ),
        ElevatedButton(
          onPressed: () => Navigator.pop(ctx, true),
          child: const Text('훈련 진행'),
        ),
      ],
    ),
  );
  if (confirmed != true || !context.mounted) return;
  final sessionId = (trainingData?['sessionId'] ?? '').toString();
  Navigator.push(context, MaterialPageRoute(
    builder: (_) => TGTimerScreenModular(
      sessionId: sessionId.isNotEmpty ? sessionId : 'temp',
      fallbackData: TrainingSession.fromMap(
        trainingData ?? {},
        sessionId.isNotEmpty ? sessionId : 'temp',
      ),
    ),
  ));
},
```

### 주의 사항
- 게이팅 추가하지 **말 것**. 커뮤니티 훈련은 무료 사용자도 사용 가능한 기능 (PaymentScreen `features` 의 `isFree: true`).
- `TGTimerScreenModular` 자체는 무료 진입 가능해야 함. 단 작업 3 처럼 **자기 캘린더에 저장된 훈련** 시작은 별개.

### 변경 파일
- `lib/common/community/screens/post_detail_screen.dart` — onTap 핸들러 다이얼로그 추가

---

## 🔴 작업 3. 캘린더 훈련 시작 — 프리미엄 게이팅

### 사용자 의도
> "결제를 한 사람이 미리 캘린더에 다 저장을 해놓고 결제가 풀렸을 때
> 저장된 캘린더 훈련들은 볼 수는 있지만 그 내용으로 훈련은 불가해야해"

→ 캘린더 **목록/상세 보기는 누구나 가능**, **시작 (timer 진입)은 프리미엄만**.

### 현재 동작
**위치**: `lib/common/calendar/calendar_screen.dart:457` (`_startWorkout`)

```dart
void _startWorkout(TrainingItem training) {
  if (training.sessionId == null || training.sessionId!.isEmpty) {
    // ... sessionId 없을 때 에러 표시
    return;
  }
  // 타이머 화면으로 이동
  Navigator.push(context, MaterialPageRoute(
    builder: (context) => TGTimerScreenModular(
      sessionId: training.sessionId!,
    ),
  ));
}
```

→ isPremium 체크 없음. 결제 만료 후에도 시작 가능.

### 목표 동작

| 상태 | 캘린더 훈련 표시 | "훈련 시작" 버튼 누르면 |
|---|---|---|
| 비구독자 | 정상 표시 | 프리미엄 안내 다이얼로그 → PaymentScreen 유도 |
| 구독자 | 정상 표시 | 타이머로 이동 (현재와 동일) |
| 관리자 | 정상 표시 | 타이머로 이동 |

### 수정 방안

`_startWorkout` 에 `PremiumGate.ensurePremium()` 호출 추가:

```dart
Future<void> _startWorkout(TrainingItem training) async {
  if (training.sessionId == null || training.sessionId!.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Row(
          children: [
            Icon(Icons.error, color: Colors.white),
            SizedBox(width: 8),
            Text('훈련 데이터를 찾을 수 없습니다.'),
          ],
        ),
        backgroundColor: Colors.red,
        duration: Duration(seconds: 2),
      ),
    );
    return;
  }

  // 프리미엄 게이팅 — 결제 만료 시 진입 차단 (저장된 데이터 보기는 OK)
  final ok = await PremiumGate.ensurePremium(
    context,
    featureName: '저장된 훈련 시작',
    customMessage:
        '저장된 훈련을 시작하려면 프리미엄 구독이 필요합니다.\n'
        '훈련 내용은 그대로 보관되며, 구독 후 바로 진행할 수 있습니다.',
  );
  if (!ok || !mounted) return;

  if (kDebugMode) {
    debugPrint('[캘린더] 훈련 시작 - sessionId: ${training.sessionId}');
  }

  Navigator.push(context, MaterialPageRoute(
    builder: (context) => TGTimerScreenModular(
      sessionId: training.sessionId!,
    ),
  )).then((_) => _loadCalendarData());
}
```

### 변경 파일
- `lib/common/calendar/calendar_screen.dart` — `_startWorkout` 시그니처 `Future<void>` 로 변경 + 게이팅
- `lib/common/calendar/widgets/day_workout_modal.dart` (만약 모달에서도 시작하는 경로 있으면 동일 적용)

### 주의 사항
- **TG 화면 게이팅과 충돌 주의**: 현재 `swimming_main_screen` 의 "훈련 시작하기" 와 calendar 의 "훈련 생성하기" 는 **TGGenerationScreenModular** 로 가는 경로 (커스텀 훈련 설정). `_startWorkout` 은 이미 저장된 훈련의 **타이머 진입 (TGTimerScreenModular)** 이라 별개.
- 즉:
  - TGGenerationScreenModular = "새 훈련 생성/편집" → Phase 1 에서 게이팅됨
  - TGTimerScreenModular = "타이머 실행" → Phase 2 에서 게이팅 추가 필요 (저장된 훈련 시작 시)

---

## 🔍 추가로 검토할 진입점들

`TGTimerScreenModular` 를 호출하는 모든 곳을 한번 점검해서, 프리미엄 게이팅이 일관되게 적용되는지 확인:

```bash
grep -rn "TGTimerScreenModular" lib --include="*.dart"
```

예상 호출 위치:
- `calendar_screen.dart` ─ `_startWorkout` (작업 3)
- `post_detail_screen.dart` ─ 커뮤니티 공유 훈련 (작업 2 — **무료 OK**, 게이팅 X)
- `tg_generation_screen_modular.dart` ─ 훈련 생성 후 바로 타이머 진입 (커스텀 훈련 흐름 → TG 진입 자체가 게이팅됐으니 OK)

→ **공유 훈련(작업 2) 은 게이팅 X / 캘린더 훈련(작업 3) 은 게이팅 O** 의 차이를 명확히.

---

## 🛠 작업 순서 추천

```
Phase 2.1  →  공통 다이얼로그 헬퍼 추출
              lib/core/subscription/subscription_dialogs.dart 신규
              GitHubAppBar 의 다이얼로그 본체 이전

Phase 2.2  →  more_screen.dart 결제/구독 메뉴 분기 (작업 1)
              SubscriptionService listener 등록

Phase 2.3  →  캘린더 _startWorkout 게이팅 (작업 3)

Phase 2.4  →  커뮤니티 공유 훈련 확인 다이얼로그 (작업 2)

Phase 2.5  →  flutter analyze 통과 확인 + sandbox/admin/구독자 3계정으로 통합 검증
```

---

## ✅ 검증 시나리오

각 작업 완료 후 확인:

### 작업 1 검증
- [ ] 비구독자 로그인 → More → 결제/구독 → PaymentScreen 진입
- [ ] 구독자 로그인 (kimsungeun0318@naver.com) → More → "구독 중 (관리)" 메뉴 → 구독 정보 다이얼로그
- [ ] 관리자 로그인 (kimkim7031@gmail.com) → More → "관리자 권한" 메뉴 → 관리자 안내 다이얼로그

### 작업 2 검증
- [ ] 비구독자 로그인 → 커뮤니티 → 다른 사용자 공유 훈련 카드 탭
- [ ] "훈련 진행" 다이얼로그 표시 (훈련명/거리/시간 보임)
- [ ] "훈련 진행" 버튼 → 타이머 화면 진입 + 공유된 훈련 데이터로 작동
- [ ] "취소" 버튼 → 다이얼로그 닫고 그대로

### 작업 3 검증
- [ ] 구독자 상태에서 캘린더에 훈련 저장
- [ ] 백엔드/RevenueCat 측에서 구독 만료 처리 (테스트용 — admin이 entitlement 회수하거나, sandbox 만료 대기)
- [ ] 앱 재시작 → 캘린더에 훈련 보이는지 확인 (보여야 함)
- [ ] 훈련 카드의 "훈련 시작" 버튼 → 프리미엄 안내 다이얼로그 → PaymentScreen
- [ ] 다시 결제 → 같은 훈련 시작 가능

---

## 📝 PaymentScreen Features 라벨 일관성 (선택)

현재 PaymentScreen 의 `_features` 배열에서 `isFree: true` 로 되어있는 항목들과 실제 게이팅이 일치하는지 점검:

| 라벨 | isFree | 현재 게이팅 |
|---|---|---|
| 퀵스타트 훈련 | true | 게이팅 X (정상) |
| 커뮤니티 훈련 실행 | true | 게이팅 X (정상) |
| 커스텀 훈련 설정 | false | TG 화면 진입 차단 (정상) |
| 훈련 기록 및 분석 | true | ❓ — 캘린더에 저장된 훈련 "기록 및 분석" 만 보는 건 가능, 단 "시작" 은 프리미엄 (작업 3) |
| 커뮤니티 열람 및 참여 | true | 게이팅 X (정상) |
| 광고 제거 | false | banner_ad_widget 게이팅 (정상) |
| 음성 출발 신호 변경 | false | TG 다이얼로그에서만 가능, TG 차단으로 자동 (정상) |
| 타종 타이머 / 복수선수 모드 | false | 동일 (정상) |
| 모든 프리미엄 기능 | false | (총괄 라벨) |

→ **"훈련 기록 및 분석" 의 description 을 "기록 보기는 무료, 저장된 훈련 시작은 프리미엄" 정도로 명확히 하는 게 좋음.**

또는 features 배열에 새 항목 추가:
```dart
{
  'icon': Icons.play_circle_filled,
  'text': '저장된 훈련 시작',
  'description': '캘린더에 저장한 훈련을 언제든 시작',
  'isFree': false,
},
```

---

## ⚠️ 비고 — Phase 2 와 연결되는 향후 개선

### 백엔드 게이팅 (선택)
현재 작업 3 의 게이팅은 Flutter 클라이언트에서만 처리. 결제 만료된 사용자가 클라이언트 우회로 `TGTimerScreenModular` 진입하면 백엔드 API 차단 없이 동작.

→ 백엔드 `TrainingSessionController` 등에 `@PreAuthorize("@entitlementService.isPremium(authentication.name)")` 추가하면 완전 차단.

### 만료 알림 (선택)
구독 만료 7일 전, 1일 전 푸시 알림 — RevenueCat 의 만료 webhook 활용해서 백엔드 → FCM 으로 보내는 흐름. 사용자 재구독 유도.

### 다음 단계
이 Phase 2 끝나면 다음 우선순위는:
1. App Store Connect 메타데이터 작업 (사용자 직접)
2. iOS TestFlight 빌드 / Sandbox 테스트
3. App Review 재제출
