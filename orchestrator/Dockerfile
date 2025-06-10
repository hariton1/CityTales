# -- Build Stage --
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

# -- Run Stage --
FROM maven:3.8.3-openjdk-17
WORKDIR /app
COPY --from=builder /app/target/orchestrator-*-SNAPSHOT.jar orchestrator.jar
EXPOSE 9098
ENTRYPOINT ["java", "-jar", "/app/orchestrator.jar"]
