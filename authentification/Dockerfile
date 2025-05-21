# Use a base image with Java and Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/citytales-auth-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
