# SWWIM API Server

Swimming Training Management API - 수영 훈련 관리 백엔드 서버

## 📋 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **프로젝트명** | SWWIM API Server |
| **버전** | 1.0.0-SNAPSHOT |
| **Spring Boot** | 4.0.1 |
| **Java** | 21 |
| **빌드 도구** | Maven |
| **데이터베이스** | PostgreSQL 15+ |
| **ORM** | JPA + QueryDSL |

---

## 🛠 기술 스택

### Core
- **Spring Boot 4.0.1** - 메인 프레임워크
- **Java 21** - LTS 버전
- **Maven** - 빌드 및 의존성 관리

### Database
- **PostgreSQL 15+** - 메인 데이터베이스
- **Spring Data JPA** - ORM 프레임워크
- **QueryDSL 5.1.0** - 타입 안전 쿼리
- **HikariCP** - 커넥션 풀

### Security
- **Spring Security** - 인증/인가
- **JWT (JJWT 0.12.5)** - 토큰 기반 인증
- **BCrypt** - 비밀번호 암호화

### Documentation
- **Springdoc OpenAPI 2.3.0** - API 문서 자동 생성
- **Swagger UI** - API 테스트 인터페이스

### Utilities
- **Lombok** - 보일러플레이트 코드 감소
- **Spring Boot DevTools** - 개발 편의 도구

---

## 🚀 시작하기

### 1. 사전 요구사항

- **Java 21** 이상
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Git**

### 2. PostgreSQL 설정

```sql
-- 데이터베이스 생성
CREATE DATABASE swwim;

-- 사용자 생성
CREATE USER swwim_user WITH PASSWORD 'your_password';

-- 권한 부여
GRANT ALL PRIVILEGES ON DATABASE swwim TO swwim_user;
```

### 3. 프로젝트 클론 및 설정

```bash
# 프로젝트 디렉토리로 이동
cd /home/user/swwim/backend

# application-dev.properties 수정
# src/main/resources/application-dev.properties 파일에서
# 데이터베이스 연결 정보 수정

spring.datasource.url=jdbc:postgresql://localhost:5432/swwim
spring.datasource.username=swwim_user
spring.datasource.password=your_actual_password
```

### 4. 빌드 및 실행

```bash
# 의존성 설치 및 빌드
mvn clean install

# 애플리케이션 실행 (개발 프로파일)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 또는 JAR 파일 실행
java -jar target/swwim-api-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### 5. 접속 확인

- **API 서버**: http://localhost:8080
- **Health Check**: http://localhost:8080/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

---

## 📁 프로젝트 구조

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/zalmuk/swwim/api/
│   │   │       ├── SwwimApiApplication.java    # 메인 클래스
│   │   │       ├── config/                     # 설정 클래스
│   │   │       │   ├── QueryDslConfig.java
│   │   │       │   ├── SwaggerConfig.java
│   │   │       │   └── CorsConfig.java
│   │   │       ├── controller/                 # REST API 컨트롤러
│   │   │       │   └── HealthCheckController.java
│   │   │       ├── domain/                     # JPA 엔티티
│   │   │       ├── dto/                        # DTO 클래스
│   │   │       ├── exception/                  # 예외 처리
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── ErrorResponse.java
│   │   │       ├── repository/                 # JPA Repository
│   │   │       ├── security/                   # 보안 설정
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── JwtProperties.java
│   │   │       └── service/                    # 비즈니스 로직
│   │   └── resources/
│   │       ├── application.properties          # 기본 설정
│   │       ├── application-dev.properties      # 개발 환경
│   │       └── application-prod.properties     # 운영 환경
│   └── test/
│       └── java/
├── pom.xml                                     # Maven 설정
├── .gitignore
└── README.md
```

---

## 🔧 주요 설정

### Profile 설정

| Profile | 용도 | 활성화 방법 |
|---------|------|------------|
| **dev** | 개발 환경 | `--spring.profiles.active=dev` |
| **prod** | 운영 환경 | `--spring.profiles.active=prod` |

### 환경 변수 (운영 환경)

운영 환경에서는 다음 환경 변수를 설정해야 합니다:

```bash
export DB_URL=jdbc:postgresql://your-db-host:5432/swwim
export DB_USERNAME=swwim_user
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your-256-bit-secret-key
export JWT_ACCESS_TOKEN_EXPIRATION=3600000
export JWT_REFRESH_TOKEN_EXPIRATION=2592000000
```

---

## 🧪 테스트

```bash
# 전체 테스트 실행
mvn test

# 특정 테스트 클래스 실행
mvn test -Dtest=HealthCheckControllerTest

# 테스트 건너뛰고 빌드
mvn clean install -DskipTests
```

---

## 📦 배포

### JAR 파일 생성

```bash
# 프로덕션 빌드
mvn clean package -Pprod

# 생성된 파일
ls -lh target/swwim-api-1.0.0-SNAPSHOT.jar
```

### Docker (향후 추가 예정)

```dockerfile
# Dockerfile 예시
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/swwim-api-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🔐 보안

### JWT 토큰

- **Access Token**: 1시간 유효 (3600000ms)
- **Refresh Token**: 30일 유효 (2592000000ms)
- **알고리즘**: HS256
- **시크릿 키**: 256비트 이상 필수

### 비밀번호 암호화

- **알고리즘**: BCrypt
- **강도**: 기본 10 rounds

---

## 📊 API 문서

### Swagger UI 접속

개발 서버 실행 후 http://localhost:8080/swagger-ui.html 접속

### 주요 API 엔드포인트

#### Health Check
- `GET /health` - 서버 상태 확인

#### 인증 (향후 추가 예정)
- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/auth/register` - 회원가입
- `POST /api/v1/auth/refresh` - 토큰 갱신

#### 사용자 (향후 추가 예정)
- `GET /api/v1/users/me` - 내 정보 조회
- `PUT /api/v1/users/me` - 내 정보 수정

#### 훈련 (향후 추가 예정)
- `GET /api/v1/trainings` - 훈련 목록
- `POST /api/v1/trainings` - 훈련 생성
- `GET /api/v1/trainings/{id}` - 훈련 상세
- `PUT /api/v1/trainings/{id}` - 훈련 수정
- `DELETE /api/v1/trainings/{id}` - 훈련 삭제

---

## 🐛 트러블슈팅

### QueryDSL Q클래스가 생성되지 않을 때

```bash
# QueryDSL APT 플러그인 실행
mvn clean compile

# target/generated-sources/java 확인
ls -la target/generated-sources/java/
```

### 데이터베이스 연결 실패

```bash
# PostgreSQL 상태 확인
sudo systemctl status postgresql

# 연결 테스트
psql -h localhost -U swwim_user -d swwim
```

### 포트 충돌 (8080 포트 사용 중)

```bash
# 포트 사용 프로세스 확인
lsof -i :8080

# 또는 다른 포트 사용
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

---

## 🔄 개발 워크플로우

### 1. 새로운 기능 개발

```bash
# 1. 브랜치 생성
git checkout -b feature/user-auth

# 2. 엔티티 작성 (domain/)
# 3. Repository 작성 (repository/)
# 4. Service 작성 (service/)
# 5. Controller 작성 (controller/)
# 6. DTO 작성 (dto/)

# 7. 빌드 및 테스트
mvn clean test

# 8. 실행 확인
mvn spring-boot:run

# 9. Swagger UI에서 API 테스트
# http://localhost:8080/swagger-ui.html
```

### 2. 코드 품질 유지

```bash
# 코드 포맷팅 (IntelliJ IDEA)
Ctrl + Alt + L

# 미사용 import 제거
Ctrl + Alt + O
```

---

## 📚 참고 자료

### 공식 문서
- [Spring Boot 4.0.1 문서](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [QueryDSL](http://querydsl.com/)
- [PostgreSQL](https://www.postgresql.org/docs/)
- [Springdoc OpenAPI](https://springdoc.org/)

### 프로젝트 문서
- [SWWIM 기획서](../SWWIM_기획서_최종.md)
- [PostgreSQL 스키마](../database/postgresql_schema.sql)
- [REST API 명세서](../database/REST_API_SPECIFICATION.md)
- [MyBatis vs JPA 분석](../database/MYBATIS_VS_JPA_RECOMMENDATION.md)

---

## 👥 팀

- **개발자**: SWWIM 개발팀
- **이메일**: contact@swwim.app
- **웹사이트**: https://swwim.app

---

## 📄 라이선스

Apache License 2.0

---

## 📝 버전 히스토리

### v1.0.0-SNAPSHOT (2026-01-15)
- ✅ 프로젝트 초기 설정
- ✅ Spring Boot 4.0.1 + Java 21 환경 구성
- ✅ JPA + QueryDSL 설정
- ✅ Spring Security 기본 설정
- ✅ JWT 인증 준비
- ✅ Swagger UI 통합
- ✅ Health Check API
- ⏳ 도메인 모델 개발 중...

---

**Last Updated**: 2026-01-15
