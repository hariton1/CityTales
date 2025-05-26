FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

FROM maven:3.8.3-openjdk-17 AS build
COPY --from=builder /app/target/qdrant-adapter-*-SNAPSHOT.jar QdrantAdapter.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/QdrantAdapter.jar"]
