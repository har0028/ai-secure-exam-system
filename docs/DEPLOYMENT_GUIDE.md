# Deployment Guide
## AI-Powered Secure Online Examination System

---

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 11+ | OpenJDK 11 or Oracle JDK 11 |
| Apache Maven | 3.6+ | |
| Apache Tomcat | 9.x | Must support Servlet 4.0 |
| MySQL | 8.x | Or MariaDB 10.6+ |

---

### Step 1 – Clone or extract the project

```
ai-secure-exam-system/
├── database/
│   ├── schema.sql              ← Run this first
│   └── sample_questions.csv   ← Test bulk upload
├── docs/
├── pom.xml
└── src/
```

---

### Step 2 – Create the database

```bash
mysql -u root -p < database/schema.sql
```

This creates the `ai_secure_exam_system` database with all 12 tables and seeds:
- Admin: `admin@aiexam.com` / `Admin@123`
- Student: `aarav.sharma@student.com` / `Student@123`
- Student: `priya.nair@student.com` / `Student@123`

**Change these passwords immediately in production.**

---

### Step 3 – Configure the database connection

Edit `src/main/resources/db.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/ai_secure_exam_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=YOUR_DB_USER
db.password=YOUR_DB_PASSWORD
```

---

### Step 4 – Build the WAR

```bash
mvn clean package -DskipTests
```

Output: `target/ai-secure-exam-system.war`

---

### Step 5 – Deploy to Tomcat

```bash
cp target/ai-secure-exam-system.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
```

Access at: **http://localhost:8080/ai-secure-exam-system/**

---

### Step 6 – Production hardening checklist

- [ ] Set `<secure>true</secure>` in `web.xml` session cookie-config (requires HTTPS)
- [ ] Use Tomcat connection pooling (configure `context.xml` DataSource) instead of `DriverManager`
- [ ] Store `db.properties` credentials in environment variables or Vault, not plaintext files
- [ ] Enable HTTPS via a reverse proxy (nginx/Apache) in front of Tomcat
- [ ] Configure Tomcat `maxConnections` and `connectionTimeout` for your expected concurrency
- [ ] Point the face-api.js model files to a CDN or serve from `/assets/face-api-models/` (download from https://github.com/justadudewhohacks/face-api.js-models)
- [ ] Set up a cron job or Tomcat listener to auto-flip exam status from SCHEDULED → ACTIVE → COMPLETED based on scheduled_start/end
- [ ] Configure log rotation for Tomcat's `catalina.out`

---

### face-api.js Models Setup

The proctoring engine requires face detection model files served at:

```
/assets/face-api-models/tiny_face_detector_model-weights_manifest.json
/assets/face-api-models/tiny_face_detector_model-shard1
/assets/face-api-models/face_landmark_68_tiny_model-weights_manifest.json
/assets/face-api-models/face_landmark_68_tiny_model-shard1
```

Download from: https://github.com/justadudewhohacks/face-api.js/tree/master/weights

Place them in: `src/main/webapp/assets/face-api-models/`

---

### Quick test sequence

1. Open `http://localhost:8080/ai-secure-exam-system/`
2. Admin login → create an exam → add questions (or bulk-upload the sample CSV)
3. Set exam status to `SCHEDULED`
4. Student login → click the exam → follow instructions → allow webcam/mic
5. Exam opens in fullscreen; try switching tabs to trigger proctoring
6. Submit → view result instantly
7. Admin → Monitoring (live feed) and Analytics (charts)
