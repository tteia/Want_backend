# 비교를 위한 Dockerfilek 사용 안함
FROM openjdk:11-jre-slim as builder
EXPOSE 8088
ARG JAR_FILE=target/*.jar
WORKDIR application
COPY ${JAR_FILE} application.jar
