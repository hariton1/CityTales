FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

FROM maven:3.8.3-openjdk-17 AS build
COPY --from=builder /app/target/data-scraper-*-SNAPSHOT.jar DataScraper.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/DataScraper.jar"]
