# 배포 자동화 기획

## 목적

현재 배포는 로컬에서 백엔드 JAR를 빌드한 뒤 EC2에 직접 업로드하고 SSH로 재시작하는 수동 방식이다.

목표는 비용을 거의 늘리지 않고, 기존 EC2 서버를 그대로 사용하면서 배포 실수를 줄이는 자동화 흐름을 만드는 것이다.

## 현재 구조

- 프론트엔드: Flutter 모바일 앱
- 백엔드: Spring Boot JAR
- 운영 서버: AWS EC2 `13.124.29.102`
- 실행 경로: `/home/ec2-user/swwim-api.jar`
- 시작 스크립트: `/home/ec2-user/start.sh`
- 로그: `/home/ec2-user/app.log`, `/home/ec2-user/server.log`
- 파일 저장소: AWS S3 `swwim-storage-5273`

현재 배포 명령:

```bash
cd /Users/sungeun/Developer/flutter/swwim-backend
./gradlew bootJar -x test

scp -i /Users/sungeun/Developer/flutter/swwim-key.pem \
  build/libs/swwim-api-1.0.0-SNAPSHOT.jar \
  ec2-user@13.124.29.102:/home/ec2-user/swwim-api.jar

ssh -i /Users/sungeun/Developer/flutter/swwim-key.pem ec2-user@13.124.29.102 << 'EOF'
pkill -f swwim-api || true
sleep 2
nohup bash /home/ec2-user/start.sh > /home/ec2-user/server.log 2>&1 &
EOF
```

## 추천 방향

기존 EC2를 유지하고 GitHub Actions로 자동 배포한다.

```text
deploy 브랜치에 push
-> GitHub Actions 실행
-> Gradle 빌드
-> JAR를 기존 EC2로 업로드
-> EC2에서 Spring Boot 재시작
-> 헬스체크 확인
```

이 방식은 서버를 새로 만들지 않기 때문에 EC2 비용이 추가되지 않는다.

## 브랜치 전략

- `main`: 개발 완료 코드
- `deploy`: 운영 배포용 브랜치

운영 반영 절차:

```text
main에서 검증
-> deploy로 merge
-> deploy push 시 자동 배포
```

처음에는 단순하게 `deploy` 브랜치 기준 자동 배포만 적용한다.

## 비용 영향

추가 비용 거의 없음:

- Git 브랜치 생성
- GitHub Actions에서 백엔드 빌드
- 기존 EC2로 파일 업로드
- 기존 EC2에서 프로세스 재시작

비용이 늘어나는 경우:

- EB 환경을 새로 생성
- staging EC2를 추가 생성
- RDS를 별도로 추가 생성
- Load Balancer를 새로 붙임
- Flutter Web을 S3/CloudFront에 별도 호스팅

현재 목표에서는 위 리소스를 만들지 않는다.

## GitHub Secrets 필요 항목

GitHub Secrets는 GitHub Actions에서 쓰는 비밀 환경변수다. SSH 키나 서버 주소처럼 코드에 올리면 안 되는 값을 저장한다.

- `EC2_HOST`: `13.124.29.102`
- `EC2_USER`: `ec2-user`
- `EC2_SSH_KEY`: EC2 접속용 private key 내용
- `EC2_APP_PATH`: `/home/ec2-user/swwim-api.jar`
- `EC2_START_COMMAND`: `bash /home/ec2-user/start.sh`

## 자동 배포 작업 범위

1. GitHub Actions workflow 생성
2. `deploy` 브랜치 push 트리거 설정
3. Java 21 설정
4. `./gradlew bootJar -x test` 실행
5. 빌드된 JAR EC2 업로드
6. 기존 프로세스 종료 후 재시작
7. `/health` 헬스체크 확인

헬스체크는 서버가 살아 있는지 확인하는 요청이다. 배포 후 `http://13.124.29.102:8080/health` 응답을 확인하면 된다.

## 주의 사항

- DB 스키마 변경이 있는 배포는 자동화 전에 수동 SQL 적용이 필요할 수 있다.
- 현재 `spring.jpa.hibernate.ddl-auto=validate`라서 테이블/컬럼이 없으면 서버 시작이 실패할 수 있다.
- 운영 서버 환경변수와 로컬 설정 파일을 분리해야 한다.
- `application.properties`의 시크릿 값은 장기적으로 환경변수로 옮겨야 한다.
- 배포 전 `flutter analyze`, 백엔드 `./gradlew test`를 최소 검증으로 유지한다.

## 나중에 고도화할 항목

- 무중단 배포
- 롤백 스크립트
- 배포 실패 시 Slack 또는 이메일 알림
- 운영/스테이징 환경 분리
- 도메인 `https://api.swwim.app` 적용
- HTTPS 인증서 적용
- EB 또는 Docker 전환 검토

## 결론

지금 단계에서는 EB로 새 환경을 만들기보다 기존 EC2를 유지한 채 GitHub Actions 자동 배포를 붙이는 방식이 가장 적합하다.

비용은 거의 그대로 유지하면서 수동 업로드와 재시작 실수를 줄일 수 있다.
