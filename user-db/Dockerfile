FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY ../user-db/pom.xml .
COPY ../user-db/src ./src

RUN mvn clean package -DskipTests
FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=build /app/target/user-db.jar .
EXPOSE 7575
CMD ["java", "-jar", "user-db.jar"]