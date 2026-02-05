# Gradle Kotlin DSL 사용 가이드

## 🎯 왜 Gradle + Kotlin DSL?

### Maven 대비 장점
- ✅ **빌드 속도 빠름** - 증분 빌드, 캐싱
- ✅ **타입 안전** - Kotlin DSL로 컴파일 타임 검증
- ✅ **간결한 문법** - XML보다 읽기 쉬움
- ✅ **강력한 플러그인 시스템**
- ✅ **멀티 모듈 프로젝트에 최적**

---

## 📁 주요 파일

### build.gradle.kts
프로젝트 빌드 스크립트 (Maven의 pom.xml과 동일)

### settings.gradle.kts
프로젝트 설정 파일 (프로젝트명 등)

### gradle/wrapper/
Gradle Wrapper 파일들 - 특정 Gradle 버전을 다운로드하고 사용

### gradlew / gradlew.bat
Gradle Wrapper 실행 스크립트 (Unix/Windows)

---

## 🚀 Gradle 명령어

### 빌드 관련
```bash
# 클린 빌드
./gradlew clean

# 빌드 (컴파일 + 테스트)
./gradlew build

# 빌드 (테스트 제외)
./gradlew build -x test

# JAR 파일만 생성
./gradlew bootJar
```

### 실행
```bash
# Spring Boot 애플리케이션 실행
./gradlew bootRun

# dev 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests UserServiceTest

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 의존성 관리
```bash
# 의존성 목록 확인
./gradlew dependencies

# 의존성 트리 확인
./gradlew dependencies --configuration runtimeClasspath

# 의존성 업데이트 확인
./gradlew dependencyUpdates
```

### 정보 확인
```bash
# 프로젝트 정보
./gradlew projects

# Task 목록
./gradlew tasks

# Task 상세 정보
./gradlew tasks --all
```

---

## 🔧 IntelliJ IDEA 설정

### 1. Gradle 프로젝트로 인식시키기

**자동 인식:**
- IntelliJ가 `build.gradle.kts` 파일을 발견하면 자동으로 Gradle 프로젝트로 인식
- 우측 하단에 "Load Gradle Project" 알림 → 클릭

**수동 설정:**
1. `File` → `New` → `Project from Existing Sources`
2. `build.gradle.kts` 선택
3. `Import project from external model` → `Gradle` 선택
4. 설정 확인 후 `OK`

### 2. Gradle JVM 설정

1. `File` → `Settings` (Mac: `Preferences`)
2. `Build, Execution, Deployment` → `Build Tools` → `Gradle`
3. `Gradle JVM`: Java 21 선택
4. `Build and run using`: Gradle (권장) 또는 IntelliJ IDEA
5. `Run tests using`: Gradle (권장) 또는 IntelliJ IDEA

### 3. Gradle Tool Window

**열기:**
- `View` → `Tool Windows` → `Gradle`
- 또는 우측 사이드바에서 Gradle 아이콘 클릭

**주요 기능:**
- **Tasks** → 빌드, 실행, 테스트 등
- **Dependencies** → 의존성 트리 확인
- **Reload** 버튼 → Gradle 프로젝트 새로고침

---

## 🎨 build.gradle.kts 구조

```kotlin
// 플러그인 정의
plugins {
    id("org.springframework.boot") version "4.0.1"
    kotlin("jvm") version "2.1.0"
}

// 프로젝트 정보
group = "com.zalmuk"
version = "1.0.0-SNAPSHOT"

// Java 버전
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// 저장소 (의존성 다운로드 위치)
repositories {
    mavenCentral()
}

// 의존성
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Task 설정
tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## 📦 의존성 추가 방법

### Maven 좌표 → Gradle

**Maven (pom.xml):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Gradle (build.gradle.kts):**
```kotlin
implementation("org.springframework.boot:spring-boot-starter-web")
```

### 버전 변수 사용

```kotlin
// 변수 정의
extra["jjwtVersion"] = "0.12.5"

// 사용
dependencies {
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")
}
```

---

## 🐛 트러블슈팅

### 1. Gradle Sync 실패
```bash
# 캐시 삭제 후 재빌드
./gradlew clean build --refresh-dependencies
```

### 2. Kotlin DSL 문법 에러
- IntelliJ에서 `build.gradle.kts` 열었을 때 빨간 밑줄
- **해결:** Gradle 새로고침 (Gradle Tool Window → Reload 버튼)

### 3. QueryDSL Q클래스 생성 안 됨
```bash
# QueryDSL 생성
./gradlew clean build

# 생성 위치 확인
ls -la build/generated/source/kapt/main/
```

### 4. 포트 충돌
```bash
# 다른 포트로 실행
./gradlew bootRun --args='--server.port=8081'
```

---

## 🔄 Maven → Gradle 마이그레이션 체크리스트

- [x] `build.gradle.kts` 생성
- [x] `settings.gradle.kts` 생성
- [x] Gradle Wrapper 파일 생성
- [x] 모든 의존성 변환
- [x] QueryDSL 설정
- [x] `.gitignore` 업데이트
- [ ] `pom.xml` 백업 후 삭제 (선택)
- [ ] `.mvn/` 폴더 삭제 (선택)

---

## 📚 참고 자료

### 공식 문서
- [Gradle 공식 문서](https://docs.gradle.org/)
- [Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/gradle-plugin/)

### Gradle vs Maven
| 항목 | Maven | Gradle |
|------|-------|--------|
| 빌드 속도 | 느림 | 빠름 (증분 빌드) |
| 설정 파일 | XML | Kotlin/Groovy |
| 학습 곡선 | 쉬움 | 중간 |
| 유연성 | 낮음 | 높음 |
| 멀티 모듈 | 보통 | 우수 |

---

**작성**: 2026-01-15
**Gradle 버전**: 8.11.1
**Spring Boot**: 4.0.1
