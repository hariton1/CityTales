# ---- Build-Image ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiere das ganze Projekt (inkl. aller Module)
COPY . .

# Baue ALLE Module (wichtig: nicht nur orchestrator, sondern auch die SNAPSHOT-Dependencies!)
RUN mvn clean install -DskipTests

# ---- Run-Image ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopiere das gebaute JAR aus dem orchestrator-Modul
COPY --from=build /app/orchestrator/target/orchestrator-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
