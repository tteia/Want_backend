# 멀티 스테이지 빌드 방법 사용
# 첫번쨰 스테이지
FROM openjdk:11 as stage1
WORKDIR /app

# /app/gradlew 파일로 생성
COPY gradlew .
# /app/gradle 디렉토리로 생성
COPY gradle gradle
# /app/src 디렉토리로 생성
COPY src src
# /app/gradlew 파일로 생성
COPY build.gradle .
# gradlew 파일을 실행 가능하게 변경
COPY settings.gradle .

RUN ./gradlew bootJar

# 두번째 스테이지
FROM openjdk:11 as stage2
WORKDIR /app
# stage1에서 생성된 jar 파일을 stage2에 app.jar라는 이름으로 복사
COPY --from=stage1 /app/build/libs/*.jar app.jar

# CMD 또는 ENTRYPOINT를 사용하여 실행할 명령어를 지정
ENTRYPOINT ["java", "-jar", "app.jar"]