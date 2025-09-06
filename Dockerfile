FROM gradle:8.4-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle --no-daemon dependencies

COPY . .

RUN gradle --no-daemon clean generateProto build -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_APPLICATION_NAME=StreetWalkerPostService
ENV SERVER_PORT=8084

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]