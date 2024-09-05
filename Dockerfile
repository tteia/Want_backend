# 첫 번째 스테이지: 애플리케이션 빌드
FROM openjdk:11 as stage1
WORKDIR /app

# Gradle Wrapper와 빌드 파일 복사 (자주 변경되지 않는 파일)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle 캐시 사용을 위한 Wrapper 실행 권한 부여
RUN chmod +x gradlew

# Gradle 종속성 미리 다운로드 (소스 코드 변경 없이 종속성만 캐싱)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 (자주 변경되는 부분)
COPY src src

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# 두 번째 스테이지: 빌드된 JAR 파일로 애플리케이션 실행
FROM openjdk:11 as stage2
WORKDIR /app

# stage1에서 생성된 JAR 파일을 복사
COPY --from=stage1 /app/build/libs/*.jar app.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
