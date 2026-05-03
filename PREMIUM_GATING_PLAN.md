# 프리미엄 기능 게이팅 작업 계획

> 목적: PaymentScreen에 광고된 프리미엄 기능을 실제로 비프리미엄 사용자에게 차단
> 진행 방식: 진입점에서 `PremiumGate.ensurePremium()` 호출 → 비프리미엄이면 결제 화면 유도

---

## 📊 현재 게이팅 상태 (2026-04-30)

| # | 프리미엄 기능 | 게이팅 상태 | 비고 |
|---|---|---|---|
| 1 | 광고 제거 | ✅ 완료 (이전부터) | `lib/common/widgets/banner_ad_widget.dart` 4곳 |
| 2 | 커스텀 훈련 설정 (TG 화면) | ✅ **완료** | swimming_main + calendar 4곳 |
| 3 | 음성 출발 신호 변경 | ✅ **자동 차단** | TG 화면 안의 `TGBeepSettingsDialog`에서만 사용. TG 차단 시 자동 |
| 4 | 타종 타이머 / 복수선수 모드 | ✅ **자동 차단** | 동일 다이얼로그의 `numPeople` 옵션. TG 차단 시 자동. PaymentScreen에서 두 항목 → 한 항목으로 통합됨 |
| 5 | 관리자 자동 통과 | ✅ **완료** | `SubscriptionService._isAdmin` 캐시 + `PremiumGate` 안전망 |

---

## 🛠 공통 인프라 — 이미 만들어둔 헬퍼

### `lib/core/subscription/premium_gate.dart`

```dart
PremiumGate.ensurePremium(
  context,
  featureName: '기능명',
  customMessage: '선택사항',
)
```

- 프리미엄이면 즉시 `true` 반환 → 호출자가 원래 동작 진행
- 비프리미엄이면 안내 다이얼로그(`프리미엄 전용 기능` + 가격 안내) → "구독하기" 버튼으로 PaymentScreen 이동
- 결제 후 다시 isPremium 체크 → 결제 성공 시 `true`, 아니면 `false`

이 헬퍼를 모든 진입점에서 호출하는 일관된 패턴으로 게이팅.

---

## ✅ 1. 커스텀 훈련 설정 — 완료된 작업 (참고용)

게이팅이 추가된 4개 진입점:

| 파일 | 위치 | 동작 |
|---|---|---|
| `lib/features/swimming/presentation/screens/swimming_main_screen.dart` | "훈련 시작하기" 버튼 | `TGGenerationScreenModular` 진입 차단 |
| `lib/common/calendar/calendar_screen.dart` | `_handleFabAction("훈련 생성하기")` | FAB 메뉴에서 훈련 생성 차단 |
| `lib/common/calendar/calendar_screen.dart` | `onAddTraining` 콜백 (Day Workout Modal) | 일별 훈련 추가 차단 |
| `lib/common/calendar/calendar_screen.dart` | `_editWorkout()` | 훈련 수정 차단 |

### 적용 패턴 예시

```dart
onTap: () async {
  final ok = await PremiumGate.ensurePremium(
    context,
    featureName: '커스텀 훈련 설정',
  );
  if (!ok || !context.mounted) return;
  Navigator.push(
    context,
    MaterialPageRoute(builder: (_) => const TGGenerationScreenModular()),
  );
},
```

---

## ❌ 2. 음성 출발 신호 변경 — 작업 필요

### 코드 위치 파악

```bash
$ grep -rn "selectedSound\|beepSound\|음성" lib --include="*.dart" \
    | grep -v "test\|comparison\|payment_screen"
```

주요 위치:
- `lib/features/training_generation/data/repositories/tg_audio_service.dart` — 사운드 재생 서비스
- `lib/features/training_generation/application/training_notifier.dart` — `selectedSound` state
- `lib/features/training_generation/presentation/screens/tg_beep_settings_screen.dart` — **소리 선택 화면 (가장 유력한 진입점)**

### 정책 결정 필요 사항

게이팅 방식 두 가지 후보:

**방식 A. 사운드 선택 화면 자체 차단**
- `tg_beep_settings_screen.dart` 진입 시 `PremiumGate.ensurePremium()`
- 비프리미엄은 기본 사운드(`ppppig.mp3`)로 강제 고정
- 장점: 단순. 단점: 무료 사용자는 사운드 선택 자체 못함

**방식 B. 기본 사운드 외 선택 시에만 차단**
- 화면은 진입 가능
- 사용자가 기본 외 다른 사운드 탭하면 그때 게이팅 다이얼로그
- 장점: 무료 사용자가 둘러볼 수는 있음. 단점: 코드 살짝 복잡

**추천: 방식 A** (단순하고 의도가 명확).

### 작업 절차

1. `tg_beep_settings_screen.dart` 열기
2. `initState` 또는 `build` 진입 시점에 `PremiumGate.ensurePremium(context, featureName: '음성 출발 신호 변경')` 호출
3. `false` 반환되면 `Navigator.pop(context)`로 즉시 빠져나가기
4. 또는 호출자(설정 페이지에서 이 화면으로 이동하는 곳) 쪽에서 게이팅

호출자 위치도 찾아야 함:
```bash
grep -rn "TgBeepSettingsScreen\|tg_beep_settings" lib --include="*.dart"
```

---

## ❌ 3. 타종 타이머 — 작업 필요

### 정의 확인 필요

"타종 타이머"가 정확히 뭔지 코드상 명확하지 않음. 예상되는 의미:
- (a) 일정 간격마다 종소리(beep) 울리는 타이머 모드
- (b) 여러 선수가 시간차로 출발할 때 각 출발 신호를 종처럼 울리는 모드 → 복수선수 모드와 연관

### 코드 위치 후보

```bash
$ grep -rn "타종\|chime\|bell" lib --include="*.dart"
```

이 키워드가 코드에 거의 없음. 즉:
- 화면상 광고만 있고 실제 기능이 미구현이거나
- 다른 이름으로 구현됨 (예: `IntervalTimer`, `LapTimer`)

### 정책 결정 필요 사항

- "타종 타이머"의 정확한 정의가 무엇인지?
- 이미 구현된 기능인지 또는 추후 구현 예정인지?
- 만약 구현됐다면 어떤 화면에서 접근하는지?

→ **사용자에게 질문 필요**. 답을 듣고 진행.

---

## ❌ 4. 복수선수 모드 — 작업 필요

### 코드 위치 파악

```bash
$ grep -rn "복수선수\|multi.*swimmer\|swimmerCount\|선수\s*수" lib --include="*.dart"
```

주요 위치 후보:
- `tg_timer_screen_modular.dart` — 타이머 화면에서 선수 수 설정?
- `tg_generation_detail_screen.dart` — 훈련 생성 시 옵션?

### 정책 결정 필요 사항

- 복수선수 모드의 정확한 동작이 무엇인지?
  - 동시에 여러 선수가 같은 훈련 진행?
  - 시간차로 출발?
  - 각자 다른 훈련을 동시에?
- 어떤 화면에서 활성화하는지?
- 무료 사용자는 1명만 가능, 프리미엄은 N명? 또는 무료는 단일선수 모드 자체만?

→ **사용자에게 질문 필요**.

---

## 🎯 다음 작업 순서 제안

### Phase 1. 정책 확정 (사용자 결정 필요)
1. **음성 출발 신호 변경**: 방식 A (화면 차단) vs 방식 B (선택 시 차단) 결정
2. **타종 타이머**: 정확한 기능 정의 + 코드 위치 확인
3. **복수선수 모드**: 정확한 동작 정의 + 코드 위치 확인

### Phase 2. 코드 위치 정밀 분석
- 위 정의를 바탕으로 진입점 파일/라인 식별
- 게이팅을 진입 시점에 할지, 특정 옵션 선택 시점에 할지 결정

### Phase 3. 게이팅 적용
- 각 진입점에 `PremiumGate.ensurePremium()` 호출 추가
- 1번 커스텀 훈련 설정과 동일한 패턴

### Phase 4. 통합 검증
- `flutter analyze` 통과 확인
- Sandbox 결제 후 모든 프리미엄 기능 정상 진입 확인
- 비프리미엄 계정으로 모든 기능 접근 시 게이팅 다이얼로그 확인

---

## 🔎 백엔드 게이팅에 대한 별도 검토 필요

현재 백엔드 API에는 premium 게이팅이 없음 (AdminController 단순 조회용 제외).

```bash
$ grep "isPremium\|hasEntitlement" src/main/java/**/Controller.java
src/main/java/com/zalmuk/swwim/api/controller/AdminController.java:47
```

→ 무료 사용자가 클라이언트 우회로 백엔드 API를 직접 호출하면 **이론상 프리미엄 기능 사용 가능**.

### 검토 필요
- 어떤 API가 프리미엄 전용인가? (예: 커스텀 훈련 세션 생성?)
- Spring Security에 `@PreAuthorize("@entitlementService.isPremium(authentication.name)")` 또는 비슷한 어노테이션 추가
- 또는 `EntitlementService.isPremium()` 호출을 컨트롤러/서비스에 추가

이건 **App Store 심사 통과 후** 차차 보강하면 충분 (앱 우회 공격은 흔치 않음). 단, 결제 회피 우려가 크다면 우선순위 ↑.

---

## 🗂 변경된 파일 목록 (참고용)

이번 작업에서 변경된 파일:

```
lib/core/subscription/premium_gate.dart                          (신규)
lib/features/swimming/presentation/screens/swimming_main_screen.dart  (수정)
lib/common/calendar/calendar_screen.dart                         (수정)
```

다음 작업에서 변경 예정인 파일:

```
lib/features/training_generation/presentation/screens/tg_beep_settings_screen.dart  (예정)
lib/features/training_generation/presentation/screens/tg_generation_screen_modular.dart  (검토)
lib/features/training_generation/presentation/screens/tg_generation_detail_screen.dart  (검토)
lib/features/training_generation/presentation/screens/tg_timer_screen_modular.dart  (검토)
```

---

## 💡 빠른 적용 체크리스트

다음 번 작업 시작할 때:

- [ ] 이 문서에서 미완료 기능(2, 3, 4번) 확인
- [ ] 사용자에게 정책 결정 사항(음성/타종/복수선수) 질문
- [ ] 답변 받은 후 코드 위치 정밀 grep
- [ ] `PremiumGate.ensurePremium()` 적용
- [ ] `flutter analyze` 통과 확인
- [ ] Sandbox 결제 후 검증
- [ ] (선택) 백엔드 API 게이팅 추가
