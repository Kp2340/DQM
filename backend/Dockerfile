FROM maven:3.8.6-eclipse-temurin-17 AS build
RUN /backend mvn clean package -DskipTests
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /backend/target/*.jar app.jar
EXPOSE 3001
ENTRYPOINT ["java", "-jar", "app.jar"]
