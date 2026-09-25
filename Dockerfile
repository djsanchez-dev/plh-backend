# ---- Build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Descarga de dependencias en capa aparte para cachear el build
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

# ---- Runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 1001 spring
COPY --from=build /app/target/*.jar app.jar
USER spring

ENV JAVA_OPTS="-XX:MaxRAMPercentage=70.0 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
