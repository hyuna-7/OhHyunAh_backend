# 빌드
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# gradle wrapper 복사
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# gradlew 실행 권한 추가
RUN chmod +x ./gradlew

# 의존성 캐시
RUN ./gradlew dependencies

# 전체 소스 복사 후 bootJar
COPY . .
RUN ./gradlew bootJar

# 런타임
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
