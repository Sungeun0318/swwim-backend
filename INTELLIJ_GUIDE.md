# IntelliJ IDEA Run Configuration
# Spring Boot 애플리케이션 실행 설정 (Gradle + Kotlin DSL)

## 실행 방법

### 방법 1: Gradle (추천)
1. 우측 Gradle 패널 열기 (View → Tool Windows → Gradle)
2. swwim-api → Tasks → build → clean 더블클릭
3. swwim-api → Tasks → build → build 더블클릭
4. swwim-api → Tasks → application → bootRun 더블클릭

또는 터미널에서:
```bash
./gradlew clean build
./gradlew bootRun
```

### 방법 2: Run Configuration
1. 상단 메뉴: Run → Edit Configurations...
2. 좌측 상단 + 버튼 → Spring Boot 선택
3. 설정:
   - Name: SwwimApiApplication
   - Main class: com.zalmuk.swwim.api.SwwimApiApplication
   - Active profiles: dev
   - Environment variables: (비워두기)
4. OK 클릭
5. 우측 상단 ▶ Run 버튼 클릭

### 방법 3: 직접 실행
1. src/main/java/com/zalmuk/swwim/api/SwwimApiApplication.java 열기
2. main 메서드 옆 초록색 ▶ 버튼 클릭
3. "Run 'SwwimApiApplication'" 선택

## 주요 단축키

- **Shift + F10**: 현재 Run Configuration 실행
- **Ctrl + F9**: 빌드
- **Ctrl + Shift + F10**: 현재 파일 실행
- **Shift + F9**: 디버그 모드 실행

## 접속 URL

실행 후:
- Health Check: http://localhost:8080/health
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Profile 설정

현재 활성 프로파일: dev (application-dev.properties)

변경하려면:
1. Run → Edit Configurations
2. Active profiles: prod 로 변경
