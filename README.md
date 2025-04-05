## 프로젝트 설명

## 기술 스택 (필수 사용 기술)

- **Spring Boot** (Java 17 기반)
- **MySQL** (데이터베이스)
- **Docker** (컨테이너화)
- **JPA** (데이터베이스 연동)
- **JUnit** / **Mockito** (테스트)

## 실행 방법

### 1. Docker Compose

### 1.1. 프로젝트 클론

```
git clone https://github.com/hyuna-7/OhHyunAh_backend.git
cd OhHyunAh_backend
```

### 1.2. Docker Compose 실행

```
docker compose -f compose.yaml up --build
```

### 1.3. 실행 확인

1. localhost:8080에서 서비스됩니다.
2. 아래 URL에서 대략적인 API 명세를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui/index.html
```

### 사용 라이브러리 및 오픈소스

1. org.springframework.boot:spring-boot-starter-validation
    * 목적: 데이터 검증을 위한 라이브러리

2. org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6
    * 목적: Swagger UI를 통한 API 문서화

### 기타 설명

* Header로 받는 MEMBER_ID는 회원 또는 인증 서버가 따로 있다고 가정하고 받아온 헤더입니다.
* Account는 계좌, Transfer는 이체/입금/송금시 기록되는 거래 내역입니다.
* Command/Query를 나누어 코드단에서 CQRS를 분리하고자 하였습니다.
* Account를 Root Aggregate로, 하위 Aggergate로 Transfer 선정하였습니다.
* 동시성 이슈를 방지를 위해 비관적 락을 적용하였습니다.
* TODO 개선 사항
    * 입금/출금/이체 분리
    * EDA 통해 느슨한 결합도로 확장성 확보
    * 로그성 데이터 저장
    * 다국어 처리
    * 예외 처리 고도화 / 세분화 등
