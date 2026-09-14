-- H2 Compatible Database Schema for AI Secure Examination System

CREATE TABLE IF NOT EXISTS users (
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(120)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,
    role            VARCHAR(20)   NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    profile_image   VARCHAR(255)  DEFAULT NULL,
    reset_token     VARCHAR(255)  DEFAULT NULL,
    reset_token_expiry DATETIME  DEFAULT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS students (
    student_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    roll_number     VARCHAR(50) UNIQUE,
    course          VARCHAR(120),
    department      VARCHAR(120),
    phone           VARCHAR(20),
    date_of_birth   DATE,
    address         VARCHAR(255),
    risk_level      VARCHAR(20) NOT NULL DEFAULT 'LOW',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admins (
    admin_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    designation     VARCHAR(120) DEFAULT 'System Administrator',
    department      VARCHAR(120),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admins_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS exams (
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
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by      BIGINT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exams_admin FOREIGN KEY (created_by) REFERENCES admins(admin_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS questions (
    question_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    question_text   TEXT NOT NULL,
    question_type   VARCHAR(20) NOT NULL DEFAULT 'MCQ',
    option_a        VARCHAR(500),
    option_b        VARCHAR(500),
    option_c        VARCHAR(500),
    option_d        VARCHAR(500),
    correct_option  VARCHAR(10) NOT NULL,
    marks           INT NOT NULL DEFAULT 1,
    category        VARCHAR(120),
    difficulty      VARCHAR(20) DEFAULT 'MEDIUM',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS exam_attempts (
    attempt_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    student_id      BIGINT NOT NULL,
    start_time      TIMESTAMP NULL,
    end_time        TIMESTAMP NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'NOT_STARTED',
    current_risk_score INT NOT NULL DEFAULT 0,
    violation_count INT NOT NULL DEFAULT 0,
    ip_address      VARCHAR(50),
    browser_info    VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempts_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    CONSTRAINT fk_attempts_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT uq_exam_student UNIQUE (exam_id, student_id)
);

CREATE TABLE IF NOT EXISTS student_answers (
    answer_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL,
    question_id     BIGINT NOT NULL,
    selected_option VARCHAR(10) DEFAULT NULL,
    is_marked_for_review BOOLEAN DEFAULT FALSE,
    is_correct      BOOLEAN DEFAULT NULL,
    answered_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_answers_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    CONSTRAINT uq_attempt_question UNIQUE (attempt_id, question_id)
);

CREATE TABLE IF NOT EXISTS results (
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
    pass_status     VARCHAR(10) NOT NULL DEFAULT 'FAIL',
    generated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_exam FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proctoring_logs (
    log_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL,
    event_type      VARCHAR(40) NOT NULL,
    severity        VARCHAR(20) NOT NULL DEFAULT 'WARNING',
    score_impact    INT NOT NULL DEFAULT 0,
    screenshot_path VARCHAR(255) DEFAULT NULL,
    details         VARCHAR(500),
    occurred_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_logs_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS risk_analysis (
    risk_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      BIGINT NOT NULL UNIQUE,
    face_missing_count   INT NOT NULL DEFAULT 0,
    multiple_face_count  INT NOT NULL DEFAULT 0,
    tab_switch_count     INT NOT NULL DEFAULT 0,
    fullscreen_exit_count INT NOT NULL DEFAULT 0,
    phone_detect_count   INT NOT NULL DEFAULT 0,
    final_risk_score     INT NOT NULL DEFAULT 0,
    risk_level      VARCHAR(20) NOT NULL DEFAULT 'LOW',
    review_status   VARCHAR(20) NOT NULL DEFAULT 'CLEAR',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_risk_attempt FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    title           VARCHAR(150) NOT NULL,
    message         VARCHAR(500) NOT NULL,
    type            VARCHAR(20) NOT NULL DEFAULT 'INFO',
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS activity_logs (
    activity_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT DEFAULT NULL,
    action          VARCHAR(150) NOT NULL,
    description     VARCHAR(500),
    ip_address      VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Seed Data
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
