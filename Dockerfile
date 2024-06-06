FROM openjdk:21-jdk-slim

ARG APP_VERSION=dev

WORKDIR /app

COPY build/libs/docker.update.sensor-${APP_VERSION}.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]