FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /app


COPY user-db/pom.xml ./
RUN mvn dependency:go-offline

COPY user-db/src/ ./src/
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre AS build
COPY --from=builder /app/target/user-db-*-SNAPSHOT.jar UserDB.jar
EXPOSE 8090
CMD ["java", "-jar", "/UserDB.jar"]