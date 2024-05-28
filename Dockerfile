FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/docker.update.sensor-1.0.1.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]