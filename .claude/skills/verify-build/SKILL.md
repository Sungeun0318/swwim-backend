---
name: verify-build
description: Flutter analyze + Spring Boot 컴파일 검증. "verify build", "빌드 확인", "빌드 검증" 후 사용.
---

# Build 검증

## Purpose

1. Flutter 정적 분석 (dart analyze)
2. Flutter 의존성 정합성
3. Spring Boot 컴파일
4. QueryDSL Q클래스 생성 확인

## When to Run

- 코드 변경 후 커밋 전
- 새 의존성 추가 후
- 배포 전 사전 검증
- Freezed/build_runner 코드 생성 후

## Related Files

| File | Purpose |
|------|---------|
| `pubspec.yaml` | Flutter 의존성 정의 |
| `analysis_options.yaml` | Dart 린트 규칙 |
| `swwim-backend/build.gradle.kts` | Backend Gradle 빌드 설정 |
| `lib/main.dart` | Flutter 앱 진입점 |

## Workflow

### Step 1: Flutter 정적 분석

```bash
cd /Users/sungeun/Developer/flutter/swwim && flutter analyze
```

**PASS:** `No issues found!`
**FAIL:** error/warning 목록 출력 → 파일:라인 형식으로 보고

### Step 2: Flutter 의존성 확인

```bash
cd /Users/sungeun/Developer/flutter/swwim && flutter pub get 2>&1
```

**PASS:** `Got dependencies!`
**FAIL:** 의존성 충돌 메시지 → 충돌 패키지 보고

### Step 3: Backend 컴파일

```bash
cd /Users/sungeun/Developer/flutter/swwim-backend && ./gradlew compileJava 2>&1 | tail -10
```

**PASS:** `BUILD SUCCESSFUL`
**FAIL:** 컴파일 에러 → 파일:라인 형식으로 보고

### Step 4: Backend JAR 빌드

```bash
cd /Users/sungeun/Developer/flutter/swwim-backend && ./gradlew bootJar -x test 2>&1 | tail -10
```

**PASS:** `BUILD SUCCESSFUL` + `build/libs/*.jar` 존재
**FAIL:** 빌드 에러 → 상세 보고

## Output Format

```markdown
| 검사 항목 | 상태 | 상세 |
|-----------|------|------|
| Flutter Analyze | PASS/FAIL | N issues |
| Flutter Dependencies | PASS/FAIL | - |
| Backend Compile | PASS/FAIL | - |
| Backend JAR | PASS/FAIL | - |
```

## Exceptions

1. **flutter analyze warning** — info 레벨 경고는 PASS 처리 (error만 FAIL)
2. **`-x test` 스킵** — 테스트 미구현 상태이므로 JAR 빌드 시 테스트 제외 정상
3. **deprecated API 경고** — 패키지 업데이트 시점이 아니면 무시 가능
