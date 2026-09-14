# ============================================================
# Multi-Stage Dockerfile for AI-Powered Secure Exam System
# Target Platform: Render / Cloud PaaS Container Deployment
# ============================================================

# --- Stage 1: Maven Build Stage ---
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /build

# Copy dependency configuration and source code
COPY pom.xml .
COPY src ./src

# Build the production WAR package
RUN mvn clean package -DskipTests

# --- Stage 2: Tomcat 9 Runtime Stage ---
FROM tomcat:9.0-jdk11-jre-slim

# Remove default Tomcat sample web applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy compiled WAR as ROOT application context
COPY --from=builder /build/target/ai-secure-exam-system.war /usr/local/tomcat/webapps/ROOT.war

# Expose standard web application port
EXPOSE 8080

# Start Apache Tomcat in foreground mode
CMD ["catalina.sh", "run"]
