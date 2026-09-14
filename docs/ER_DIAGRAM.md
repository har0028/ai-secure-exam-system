# Entity-Relationship Diagram
## AI-Powered Secure Online Examination System

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         ai_secure_exam_system  –  ER Overview                   │
└──────────────────────────────────────────────────────────────────────────────────┘

  ┌────────────┐       ┌─────────────┐       ┌─────────────┐
  │   users    │ 1   1 │  students   │       │   admins    │
  │────────────│───────│─────────────│       │─────────────│
  │ user_id PK │       │ student_id  │       │ admin_id PK │
  │ full_name  │       │ user_id FK  │       │ user_id FK  │
  │ email UQ   │       │ roll_number │       │ designation │
  │ password   │       │ course      │       │ department  │
  │ role       │       │ department  │       └──────┬──────┘
  │ status     │       │ phone       │              │ 1
  └──────┬─────┘       │ risk_level  │              │
         │             └──────┬──────┘              │
         │ 1                  │ 1                   │ creates
         │                    │ attempts            │
         │ receives           │                     ▼ *
         ▼ *                  │              ┌──────────────┐
  ┌──────────────┐            │              │    exams     │
  │notifications │            │              │──────────────│
  │──────────────│            │              │ exam_id PK   │
  │notification  │            │              │ title        │
  │ _id PK       │            │              │ duration_min │
  │ user_id FK   │            │              │ total_marks  │
  │ title        │            │              │ passing_marks│
  │ message      │            │              │ status       │
  │ type         │            │              │ created_by FK│
  │ is_read      │            │              └──────┬───────┘
  └──────────────┘            │                     │ 1
                               │                     │ has
                               ▼ *          ┌────────┘ *
                        ┌─────────────────┐ ▼
                        │  exam_attempts  │ ┌──────────────┐
                        │─────────────────│ │  questions   │
                        │ attempt_id PK   │ │──────────────│
                        │ exam_id FK      │ │ question_id  │
                        │ student_id FK   │ │ exam_id FK   │
                        │ start_time      │ │ question_text│
                        │ end_time        │ │ question_type│
                        │ status          │ │ option_a..d  │
                        │ current_risk_   │ │ correct_opt  │
                        │  score          │ │ marks        │
                        │ violation_count │ │ difficulty   │
                        └────────┬────────┘ └──────────────┘
                                 │ 1
                     ┌───────────┼────────────────┐
                     │           │                │
                     ▼ *         ▼ 1              ▼ 1
               ┌──────────┐ ┌──────────┐  ┌──────────────┐
               │ student  │ │ results  │  │ risk_analysis│
               │ _answers │ │──────────│  │──────────────│
               │──────────│ │result_id │  │ risk_id PK   │
               │answer_id │ │attempt_id│  │ attempt_id FK│
               │attempt_id│ │exam_id   │  │ face_missing │
               │question  │ │student_id│  │ _count       │
               │ _id FK   │ │obtained  │  │ multiple_face│
               │selected  │ │ _marks   │  │ tab_switch   │
               │ _option  │ │percent   │  │ fullscreen   │
               │is_marked │ │pass_     │  │ phone_detect │
               │is_correct│ │ status   │  │ risk_score   │
               └──────────┘ └──────────┘  │ risk_level   │
                                          │ review_status│
                     │ 1                  └──────────────┘
                     ▼ *
               ┌──────────────┐     ┌──────────────────┐
               │ proctoring   │     │  activity_logs   │
               │   _logs      │     │──────────────────│
               │──────────────│     │ activity_id PK   │
               │ log_id PK    │     │ user_id FK       │
               │ attempt_id FK│     │ action           │
               │ event_type   │     │ description      │
               │ severity     │     │ ip_address       │
               │ score_impact │     │ created_at       │
               │ details      │     └──────────────────┘
               │ occurred_at  │
               └──────────────┘
```

## Key Relationships

| From         | To               | Cardinality | FK Column          |
|--------------|------------------|-------------|--------------------|
| users        | students         | 1:1         | students.user_id   |
| users        | admins           | 1:1         | admins.user_id     |
| admins       | exams            | 1:N         | exams.created_by   |
| exams        | questions        | 1:N         | questions.exam_id  |
| students     | exam_attempts    | 1:N         | exam_attempts.student_id |
| exams        | exam_attempts    | 1:N         | exam_attempts.exam_id |
| exam_attempts| student_answers  | 1:N         | student_answers.attempt_id |
| exam_attempts| results          | 1:1         | results.attempt_id |
| exam_attempts| risk_analysis    | 1:1         | risk_analysis.attempt_id |
| exam_attempts| proctoring_logs  | 1:N         | proctoring_logs.attempt_id |
| users        | notifications    | 1:N         | notifications.user_id |
| users        | activity_logs    | 1:N         | activity_logs.user_id |
| questions    | student_answers  | 1:N         | student_answers.question_id |

## Risk Scoring Rules

| Event Type       | Score Impact |
|------------------|-------------|
| FACE_MISSING     | +10         |
| LOOKING_AWAY     | +10         |
| TAB_SWITCH       | +15         |
| WINDOW_BLUR      | +15         |
| CAMERA_DISABLED  | +15         |
| FULLSCREEN_EXIT  | +20         |
| MULTIPLE_FACES   | +30         |
| PHONE_DETECTED   | +40         |

| Score Range | Risk Level |
|-------------|-----------|
| 0–25        | LOW       |
| 26–50       | MEDIUM    |
| 51–75       | HIGH      |
| 76–100      | CRITICAL → Auto-Submit |
