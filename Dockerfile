# ============================================================
# Production Tomcat 9 Dockerfile for AI-Powered Secure Exam System
# Target Platform: Render / Cloud PaaS Container Deployment
# ============================================================

FROM tomcat:9.0-jre11-slim

# Remove default Tomcat sample web applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy compiled production WAR to ROOT application context
COPY target/ai-secure-exam-system.war /usr/local/tomcat/webapps/ROOT.war

# Expose standard web application port
EXPOSE 8080

# Start Apache Tomcat in foreground mode
CMD ["catalina.sh", "run"]
