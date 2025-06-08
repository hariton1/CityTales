# ---- Build-Image ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy all pom.xml files first for better dependency caching
COPY pom.xml ./
COPY orchestrator/pom.xml orchestrator/pom.xml
COPY user-db/pom.xml user-db/pom.xml
COPY neo4j-data-access/pom.xml neo4j-data-access/pom.xml
COPY data-scraper/pom.xml data-scraper/pom.xml
COPY qdrant-adapter/pom.xml qdrant-adapter/pom.xml
COPY openai-adapter/pom.xml openai-adapter/pom.xml

RUN mvn dependency:go-offline

# Now copy everything else (source files)
COPY . .

RUN mvn clean install -pl orchestrator,user-db,neo4j-data-access -am -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/orchestrator/target/orchestrator-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
