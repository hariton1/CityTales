FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests


FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/openai-adapter-*-SNAPSHOT.jar openai-adapter.jar

EXPOSE 8078

ENTRYPOINT ["java", "-jar", "openai-adapter.jar"]
