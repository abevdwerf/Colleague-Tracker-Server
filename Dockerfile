# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-alpine

COPY target/ColleagueTracker-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "ColleagueTracker-0.0.1-SNAPSHOT.jar"]