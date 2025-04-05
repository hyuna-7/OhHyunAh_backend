# 빌드
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# gradle wrapper 전체 폴더 포함 복사
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# 의존성 다운 (캐시)
RUN ./gradlew dependencies

# 전체 소스 복사 후 bootJar build
COPY . .
RUN ./gradlew bootJar

# 런타임
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
