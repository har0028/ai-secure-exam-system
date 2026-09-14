# AI-Powered Secure Online Examination System

A production-ready, role-based online examination platform with real-time AI proctoring.
Built on Java Servlets / JSP, JDBC, MySQL, following MVC + DAO + Service-layer architecture.

---

## ✅ Complete Feature Set

### Authentication & Security
- Student self-registration and admin login (seeded accounts)
- BCrypt-hashed passwords (org.mindrot jBCrypt 0.4, $2a$ hashes, cost factor 12)
- Session-based auth, 30-minute idle timeout, session-fixation protection
- Role-based route filters: `/admin/*` restricted to ADMIN, `/student/*` to STUDENT
- Forgot/reset password (single-use token, 30-minute expiry)
- Full audit trail (every login, logout, CRUD action, exam event)

### Admin Features
- **Student Management**: add, edit, delete, search with live risk-level badges
- **Exam Management**: create, edit, delete; set duration/marks/schedule, shuffle toggles, proctoring toggle
- **Question Management**: add, edit, delete per exam; CSV bulk upload with row-level validation and skip-and-report
- **Results Dashboard**: search by student/exam, pass/fail status, proctoring review flag
- **Live Monitoring**: real-time grid of active exam sessions with per-candidate risk rings and a live violation feed (auto-refreshes every 30s)
- **Analytics**: Chart.js pass/fail doughnut + violations-by-type horizontal bar chart, summary stats
- **Settings** page with system configuration overview

### Student Features  
- Dashboard showing available exams with real data
- Exam instructions page with pre-exam device/rule checklist
- **Full Exam Engine**: countdown timer, question palette, mark-for-review, autosave every 30s, previous/next navigation, manual submit + auto-submit on timer expiry
- Instant result with score, percentage, correct/wrong/unanswered breakdown, pass/fail
- Results history list
- Notifications panel
- Profile / change-password page

### AI Proctoring Engine (proctoring.js)
- **Webcam monitoring** via MediaDevices API; reports CAMERA_DISABLED if denied
- **Face detection** (face-api.js TinyFaceDetector, sampled every 3s):
  - Face missing → WARNING → escalating risk
  - Multiple faces → CRITICAL event
  - Looking away (face off-center) → WARNING
- **Tab/window monitoring**: `visibilitychange` + `blur` events → TAB_SWITCH / WINDOW_BLUR
- **Fullscreen enforcement**: exam starts in fullscreen; every exit → FULLSCREEN_EXIT logged + warning overlay
- **Microphone monitoring**: audio-level analysis → UNUSUAL_NOISE events
- **Dynamic risk scoring** (Face Missing +10, Tab Switch +15, Fullscreen Exit +20, Multiple Faces +30, Phone Detected +40)
- **Auto-submit at score ≥ 76** (CRITICAL): exam submitted, flagged UNDER_REVIEW for admin
- Risk ring widget (animated SVG) shown to student during exam and on admin monitoring dashboard

---

## Default Credentials (change in production!)

| Role    | Email                    | Password    |
|---------|--------------------------|-------------|
| Admin   | admin@aiexam.com         | Admin@123   |
| Student | aarav.sharma@student.com | Student@123 |
| Student | priya.nair@student.com   | Student@123 |

---

## Tech Stack

- **Backend**: Java 11, Servlet 4.0, JSP, JSTL, JDBC (PreparedStatement throughout)
- **Database**: MySQL 8.x — 12 tables with FK constraints, ON DELETE CASCADE
- **Security**: jBCrypt 0.4, session filters, HttpOnly cookies, input sanitization
- **Frontend**: HTML5, CSS3 (custom design system, no Bootstrap), vanilla JS
- **AI/Proctoring**: face-api.js (CDN), Web Audio API, Fullscreen API, MediaDevices
- **Charts**: Chart.js 4 (CDN)
- **Build**: Maven, WAR packaging for Tomcat 9

---

## Quick Start

```bash
# 1. Create database
mysql -u root -p < database/schema.sql

# 2. Set your DB password
vim src/main/resources/db.properties

# 3. Download face-api.js models (needed for face detection)
#    https://github.com/justadudewhohacks/face-api.js/tree/master/weights
#    Copy tiny_face_detector + face_landmark_68_tiny files to:
#    src/main/webapp/assets/face-api-models/

# 4. Build
mvn clean package

# 5. Deploy
cp target/ai-secure-exam-system.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
# → http://localhost:8080/ai-secure-exam-system/
```

---

## Project Structure

```
src/main/java/com/aiexam/
├── model/          User, Student, Admin, Exam, Question, ExamAttempt,
│                   StudentAnswer, Result, ProctoringLog, RiskAnalysis,
│                   Notification, ActivityLog
├── dao/            UserDAO, StudentDAO, AdminDAO, ExamDAO, QuestionDAO,
│                   ExamAttemptDAO, StudentAnswerDAO, ResultDAO,
│                   ProctoringLogDAO, RiskAnalysisDAO, NotificationDAO,
│                   ActivityLogDAO
├── service/        AuthService, ExamService, StudentManagementService,
│                   QuestionService, ExamAttemptService, ProctoringService,
│                   ResultService
├── controller/     LoginServlet, RegisterServlet, LogoutServlet,
│                   ForgotPasswordServlet, ResetPasswordServlet,
│                   AdminDashboardServlet, AdminStudentServlet,
│                   AdminStudentFormServlet, AdminStudentDeleteServlet,
│                   AdminExamServlet, AdminExamFormServlet, AdminExamDeleteServlet,
│                   AdminQuestionServlet, AdminQuestionFormServlet,
│                   AdminQuestionDeleteServlet, AdminQuestionBulkUploadServlet,
│                   AdminResultsServlet, AdminMonitoringServlet,
│                   AdminAnalyticsServlet,
│                   StudentDashboardServlet, StudentExamListServlet,
│                   StudentExamInstructionsServlet, StudentExamStartServlet,
│                   StudentExamTakeServlet, StudentExamSaveAnswerServlet,
│                   StudentExamSubmitServlet, StudentExamResultServlet,
│                   StudentResultsServlet, StudentNotificationsServlet,
│                   StudentProfileServlet,
│                   ProctoringEventServlet
├── filter/         AuthFilter, AdminAuthFilter, StudentAuthFilter
└── util/           DBConnection, PasswordUtil, ValidationUtil, JsonUtil

src/main/webapp/
├── WEB-INF/web.xml
├── common/         head.jsp, admin-sidebar.jsp, student-sidebar.jsp,
│                   flash-messages.jsp
├── admin/          dashboard.jsp, students.jsp, student-form.jsp,
│                   exams.jsp, exam-form.jsp, questions.jsp, question-form.jsp,
│                   question-bulk-upload.jsp, results.jsp, monitoring.jsp,
│                   analytics.jsp, settings.jsp
├── student/        dashboard.jsp, exams.jsp, exam-instructions.jsp,
│                   exam-taking.jsp, result-detail.jsp, results.jsp,
│                   notifications.jsp, profile.jsp
├── error/          403.jsp, 404.jsp, 500.jsp
├── assets/
│   ├── css/        style.css, auth.css, dashboard.css, exam.css
│   ├── js/         main.js, exam-engine.js, proctoring.js
│   └── face-api-models/  (download separately - see above)
└── index.jsp, login.jsp, register.jsp, forgot-password.jsp, reset-password.jsp

database/
├── schema.sql              Full schema + sample data
└── sample_questions.csv    10-question bulk-upload test file

docs/
├── ER_DIAGRAM.md
└── DEPLOYMENT_GUIDE.md
```

---

See `docs/DEPLOYMENT_GUIDE.md` for production hardening checklist.
See `docs/ER_DIAGRAM.md` for the full entity-relationship diagram.
