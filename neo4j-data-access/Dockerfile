FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

FROM maven:3.8.3-openjdk-17 AS build
COPY --from=builder /app/target/neo4j-data-access-*-SNAPSHOT.jar Neo4jDataAccess.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/Neo4jDataAccess.jar"]
