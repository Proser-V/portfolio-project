# ---- Stage 1 : Build and Test ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build and run tests
RUN mvn clean package

# ---- Stage 2 : Runtime ----
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy only the final JAR
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]