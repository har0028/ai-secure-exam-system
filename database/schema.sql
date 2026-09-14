-- ============================================================
-- AI-Powered Secure Online Examination System
-- Database: ai_secure_exam_system
-- Engine: MySQL 8.x
-- ============================================================

DROP DATABASE IF EXISTS ai_secure_exam_system;
CREATE DATABASE ai_secure_exam_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_secure_exam_system;

-- ============================================================
-- 1. USERS  (base authentication table for both roles)
-- ============================================================
CREATE TABLE users (
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(120)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,           -- BCrypt hash
    role            ENUM('ADMIN','STUDENT') NOT NULL,
    status          ENUM('ACTIVE','INACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE',
    profile_image   VARCHAR(255)  DEFAULT NULL,
    reset_token     VARCHAR(255)  DEFAULT NULL,
    reset_token_expiry DATETIME  DEFAULT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ============================================================
-- 2. STUDENTS  (extends users with academic profile data)
-- ============================================================
CREATE TABLE students (
    student_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    roll_number     VARCHAR(50) UNIQUE,
    course          VARCHAR(120),
    department      VARCHAR(120),
    phone           VARCHAR(20),
    date_of_birth   DATE,
    address         VARCHAR(255),
    risk_level      ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'LOW',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 3. ADMINS  (extends users with admin metadata)
-- ============================================================
CREATE TABLE admins (
    admin_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    designation     VARCHAR(120) DEFAULT 'System Administrator',
    department      VARCHAR(120),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admins_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 4. EXAMS
-- ============================================================
CREATE TABLE exams (
    exam_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    category        VARCHAR(120),
    duration_minutes INT NOT NULL DEFAULT 60,
    total_marks     INT NOT NULL DEFAULT 100,
    passing_marks   INT NOT NULL DEFAULT 40,
    scheduled_start DATETIME,
    scheduled_end   DATETIME,
    shuffle_questions BOOLEAN DEFAULT TRUE,
    shuffle_options   BOOLEAN DEFAULT TRUE,
    proctoring_enabled BOOLEAN DEFAULT TRUE,
    status          ENUM('DRAFT','SCHEDULED','ACTIVE','COMPLETED','CANCELLED') NOT NULL DEFAULT 'DRAFT',
    created_by      BIGINT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_exams_admin FOREIGN KEY (created_by) REFERENCES admins(admin_id) ON DELETE SET NULL,
    INDEX idx_exams_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 5. QUESTIONS
-- ============================================================
CREATE TABLE questions (
    question_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    question_text   TEXT NOT NULL,
    question_type   ENUM('MCQ','TRUE_FALSE','SINGLE_CORRECT') NOT NULL DEFAULT 'MCQ',
    option_a        VARCHAR(500),
    option_b        VARCHAR(500),
    option_c        VARCHAR(500),
    option_d        VARCHAR(500),
    correct_option  ENUM('A','B','C','D') NOT NULL,
    marks           INT NOT NULL DEFAULT 1,
    category        VARCHAR(120),
    difficulty      ENUM('EASY','MEDIUM','HARD') DEFAULT 'MEDIUM',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    INDEX idx_questions_exam (exam_id)
) ENGINE=InnoDB;

-- ============================================================
-- 6. EXAM_ATTEMPTS
-- ============================================================
CREATE TABLE exam_attempts (
    attempt_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    student_id      BIGINT NOT NULL,
    start_time      TIMESTAMP NULL,
    end_time        TIMESTAMP NULL,
    status          ENUM('NOT_STARTED','IN_PROGRESS','SUBMITTED','AUTO_SUBMITTED','UNDER_REVIEW') NOT NULL DEFAULT 'NOT_STARTED',
    current_risk_score INT NOT NULL DEFAULT 0,
    violation_count INT NOT NULL DEFAULT 0,
    ip_address      VARCHAR(50),
    browser_info    VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempts_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    CONSTRAINT fk_attempts_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    UNIQUE KEY uq_exam_student (exam_id, student_id),
    INDEX idx_attempts_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 7. STUDENT_ANSWERS
-- ============================================================
CREATE TABLE student_answers (
    answer_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL,
    question_id     BIGINT NOT NULL,
    selected_option ENUM('A','B','C','D') DEFAULT NULL,
    is_marked_for_review BOOLEAN DEFAULT FALSE,
    is_correct      BOOLEAN DEFAULT NULL,
    answered_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_answers_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    UNIQUE KEY uq_attempt_question (attempt_id, question_id)
) ENGINE=InnoDB;

-- ============================================================
-- 8. RESULTS
-- ============================================================
CREATE TABLE results (
    result_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL UNIQUE,
    exam_id         BIGINT NOT NULL,
    student_id      BIGINT NOT NULL,
    total_marks     INT NOT NULL,
    obtained_marks  INT NOT NULL DEFAULT 0,
    correct_answers INT NOT NULL DEFAULT 0,
    wrong_answers   INT NOT NULL DEFAULT 0,
    unanswered      INT NOT NULL DEFAULT 0,
    percentage      DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    pass_status     ENUM('PASS','FAIL') NOT NULL DEFAULT 'FAIL',
    generated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 9. PROCTORING_LOGS  (every AI/violation event during an attempt)
-- ============================================================
CREATE TABLE proctoring_logs (
    log_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL,
    event_type      ENUM('FACE_MISSING','MULTIPLE_FACES','LOOKING_AWAY','PHONE_DETECTED',
                         'TAB_SWITCH','WINDOW_BLUR','FULLSCREEN_EXIT','CAMERA_DISABLED',
                         'MIC_DISABLED','UNUSUAL_NOISE','OTHER') NOT NULL,
    severity        ENUM('INFO','WARNING','SERIOUS','CRITICAL') NOT NULL DEFAULT 'WARNING',
    score_impact    INT NOT NULL DEFAULT 0,
    screenshot_path VARCHAR(255) DEFAULT NULL,
    details         VARCHAR(500),
    occurred_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_logs_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    INDEX idx_logs_attempt (attempt_id),
    INDEX idx_logs_type (event_type)
) ENGINE=InnoDB;

-- ============================================================
-- 10. RISK_ANALYSIS  (aggregated risk snapshot per attempt)
-- ============================================================
CREATE TABLE risk_analysis (
    risk_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL UNIQUE,
    face_missing_count   INT NOT NULL DEFAULT 0,
    multiple_face_count  INT NOT NULL DEFAULT 0,
    tab_switch_count     INT NOT NULL DEFAULT 0,
    fullscreen_exit_count INT NOT NULL DEFAULT 0,
    phone_detect_count   INT NOT NULL DEFAULT 0,
    final_risk_score     INT NOT NULL DEFAULT 0,
    risk_level      ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'LOW',
    review_status   ENUM('CLEAR','UNDER_REVIEW','REVIEWED') NOT NULL DEFAULT 'CLEAR',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 11. NOTIFICATIONS
-- ============================================================
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    title           VARCHAR(150) NOT NULL,
    message         VARCHAR(500) NOT NULL,
    type            ENUM('INFO','EXAM','RESULT','WARNING','SYSTEM') NOT NULL DEFAULT 'INFO',
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id, is_read)
) ENGINE=InnoDB;

-- ============================================================
-- 12. ACTIVITY_LOGS  (system-wide audit trail)
-- ============================================================
CREATE TABLE activity_logs (
    activity_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT DEFAULT NULL,
    action          VARCHAR(150) NOT NULL,
    description     VARCHAR(500),
    ip_address      VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_activity_created (created_at)
) ENGINE=InnoDB;

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Default admin login : admin@aiexam.com   / Admin@123
-- Default student logins: aarav.sharma@student.com / Student@123
--                          priya.nair@student.com   / Student@123
-- (Hashes below are real BCrypt $2a$ hashes, generated and verified against
--  the actual org.mindrot:jbcrypt:0.4 library used by this project - see
--  PasswordUtil.java. jBCrypt 0.4 only accepts the $2a$ salt revision, not
--  the $2b$ default used by some other bcrypt implementations.)
INSERT INTO users (full_name, email, password_hash, role, status) VALUES
('System Administrator', 'admin@aiexam.com', '$2a$12$Kv2WzadgEyBrT3D5mfIy2O0VtR2iRERHYpNOL2EcZpFeStXBZBgkS', 'ADMIN', 'ACTIVE'),
('Aarav Sharma', 'aarav.sharma@student.com', '$2a$12$CIfVI.dhvEXSojydXh6Q3uwlYvVAJKJp4/L9TNPyDrrTqj40y7BHO', 'STUDENT', 'ACTIVE'),
('Priya Nair', 'priya.nair@student.com', '$2a$12$CIfVI.dhvEXSojydXh6Q3uwlYvVAJKJp4/L9TNPyDrrTqj40y7BHO', 'STUDENT', 'ACTIVE');

INSERT INTO admins (user_id, designation, department) VALUES
(1, 'Chief Examination Controller', 'Examination Cell');

INSERT INTO students (user_id, roll_number, course, department, phone) VALUES
(2, 'CS2023001', 'B.Tech', 'Computer Science', '9876543210'),
(3, 'CS2023002', 'B.Tech', 'Computer Science', '9876543211');

INSERT INTO exams (title, description, category, duration_minutes, total_marks, passing_marks, scheduled_start, scheduled_end, status, created_by) VALUES
('Data Structures Fundamentals', 'Covers arrays, linked lists, stacks, queues and trees.', 'Computer Science', 60, 50, 20, '2026-06-20 10:00:00', '2026-06-20 18:00:00', 'SCHEDULED', 1),
('Database Management Systems', 'SQL, normalization, and transactions.', 'Computer Science', 45, 40, 16, '2026-06-22 10:00:00', '2026-06-22 18:00:00', 'SCHEDULED', 1);

INSERT INTO questions (exam_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_option, marks, category, difficulty) VALUES
(1, 'Which data structure uses LIFO order?', 'MCQ', 'Queue', 'Stack', 'Linked List', 'Tree', 'B', 2, 'Data Structures', 'EASY'),
(1, 'What is the time complexity of binary search?', 'MCQ', 'O(n)', 'O(log n)', 'O(n^2)', 'O(1)', 'B', 2, 'Algorithms', 'MEDIUM'),
(1, 'A binary tree node has at most how many children?', 'MCQ', '1', '2', '3', '4', 'B', 2, 'Trees', 'EASY'),
(2, 'Which SQL clause is used to filter grouped rows?', 'MCQ', 'WHERE', 'HAVING', 'GROUP BY', 'ORDER BY', 'B', 2, 'SQL', 'MEDIUM'),
(2, 'Normalization primarily reduces:', 'MCQ', 'Data redundancy', 'Query speed', 'Index size', 'Table count', 'A', 2, 'DBMS Theory', 'MEDIUM');

INSERT INTO notifications (user_id, title, message, type) VALUES
(2, 'New Exam Scheduled', 'Data Structures Fundamentals has been scheduled for June 20.', 'EXAM'),
(3, 'New Exam Scheduled', 'Database Management Systems has been scheduled for June 22.', 'EXAM');
