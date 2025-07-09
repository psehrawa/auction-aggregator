# Multi-stage build for Java Spring Boot application
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Set working directory
WORKDIR /app

# Copy parent pom
COPY ../pom.xml /app/parent-pom.xml

# Copy common module
COPY ../common /app/common

# Copy auction service
COPY . /app/auction-service

# Build the application
WORKDIR /app/auction-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create user
RUN addgroup -g 1000 spring && \
    adduser -u 1000 -G spring -s /bin/sh -D spring

# Set working directory
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/auction-service/target/*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]