package com.aiexam.service;

import com.aiexam.dao.ActivityLogDAO;
import com.aiexam.dao.QuestionDAO;
import com.aiexam.model.Question;
import com.aiexam.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuestionService {

    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    private static final Set<String> VALID_TYPES = Set.of("MCQ", "TRUE_FALSE", "SINGLE_CORRECT");
    private static final Set<String> VALID_OPTIONS = Set.of("A", "B", "C", "D");
    private static final Set<String> VALID_DIFFICULTY = Set.of("EASY", "MEDIUM", "HARD");

    public static class Result {
        public final boolean success;
        public final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static class BulkUploadResult {
        public final boolean success;
        public final String message;
        public final int insertedCount;
        public final List<String> rowErrors;

        public BulkUploadResult(boolean success, String message, int insertedCount, List<String> rowErrors) {
            this.success = success;
            this.message = message;
            this.insertedCount = insertedCount;
            this.rowErrors = rowErrors;
        }
    }

    public Result createQuestion(long examId, String questionText, String questionType, String optionA,
                                  String optionB, String optionC, String optionD, String correctOption,
                                  int marks, String category, String difficulty,
                                  long actingUserId, String ip) {
        String validationError = validateQuestionFields(questionText, questionType, optionA, optionB,
                optionC, optionD, correctOption, marks);
        if (validationError != null) {
            return new Result(false, validationError);
        }
        try {
            Question q = buildQuestion(examId, questionText, questionType, optionA, optionB, optionC,
                    optionD, correctOption, marks, category, difficulty);
            long id = questionDAO.insert(q);
            if (id <= 0) {
                return new Result(false, "Failed to create question.");
            }
            activityLogDAO.log(actingUserId, "ADMIN_CREATE_QUESTION",
                    "Added question to exam #" + examId, ip);
            return new Result(true, "Question added successfully.");
        } catch (SQLException e) {
            return new Result(false, "A database error occurred while creating the question.");
        }
    }

    public Result updateQuestion(long questionId, String questionText, String questionType, String optionA,
                                  String optionB, String optionC, String optionD, String correctOption,
                                  int marks, String category, String difficulty,
                                  long actingUserId, String ip) {
        String validationError = validateQuestionFields(questionText, questionType, optionA, optionB,
                optionC, optionD, correctOption, marks);
        if (validationError != null) {
            return new Result(false, validationError);
        }
        try {
            Question q = questionDAO.findById(questionId);
            if (q == null) {
                return new Result(false, "Question not found.");
            }
            q.setQuestionText(questionText.trim());
            q.setQuestionType(questionType);
            q.setOptionA(optionA);
            q.setOptionB(optionB);
            q.setOptionC(optionC);
            q.setOptionD(optionD);
            q.setCorrectOption(correctOption);
            q.setMarks(marks);
            q.setCategory(category);
            q.setDifficulty(difficulty);

            boolean updated = questionDAO.update(q);
            if (!updated) {
                return new Result(false, "Failed to update question.");
            }
            activityLogDAO.log(actingUserId, "ADMIN_UPDATE_QUESTION", "Updated question #" + questionId, ip);
            return new Result(true, "Question updated successfully.");
        } catch (SQLException e) {
            return new Result(false, "A database error occurred while updating the question.");
        }
    }

    public Result deleteQuestion(long questionId, long actingUserId, String ip) {
        try {
            boolean deleted = questionDAO.delete(questionId);
            if (!deleted) {
                return new Result(false, "Question not found or already deleted.");
            }
            activityLogDAO.log(actingUserId, "ADMIN_DELETE_QUESTION", "Deleted question #" + questionId, ip);
            return new Result(true, "Question deleted successfully.");
        } catch (SQLException e) {
            return new Result(false, "A database error occurred while deleting the question.");
        }
    }

    public List<Question> listByExam(long examId) throws SQLException {
        return questionDAO.findByExamId(examId);
    }

    public Question getById(long questionId) throws SQLException {
        return questionDAO.findById(questionId);
    }

    public List<String> listDistinctCategories() throws SQLException {
        return questionDAO.findDistinctCategories();
    }

    /**
     * Parses a bulk-upload CSV and inserts every valid row in a single batch.
     * Expected header (case-insensitive, order-independent):
     * question_text,question_type,option_a,option_b,option_c,option_d,correct_option,marks,category,difficulty
     * Rows that fail validation are skipped and reported back individually;
     * the batch insert only proceeds with the rows that passed validation.
     */
    public BulkUploadResult bulkUploadFromCsv(long examId, BufferedReader reader,
                                               long actingUserId, String ip) {
        List<Question> validRows = new ArrayList<>();
        List<String> rowErrors = new ArrayList<>();

        try {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new BulkUploadResult(false, "The uploaded file is empty.", 0, rowErrors);
            }
            List<String> headers = parseCsvLine(headerLine);
            for (int i = 0; i < headers.size(); i++) {
                headers.set(i, headers.get(i).trim().toLowerCase());
            }

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;
                List<String> cols = parseCsvLine(line);

                String questionText = getCol(cols, headers, "question_text");
                String questionType = getCol(cols, headers, "question_type");
                String optionA = getCol(cols, headers, "option_a");
                String optionB = getCol(cols, headers, "option_b");
                String optionC = getCol(cols, headers, "option_c");
                String optionD = getCol(cols, headers, "option_d");
                String correctOption = getCol(cols, headers, "correct_option");
                String marksStr = getCol(cols, headers, "marks");
                String category = getCol(cols, headers, "category");
                String difficulty = getCol(cols, headers, "difficulty");

                int marks;
                try {
                    marks = ValidationUtil.isNotBlank(marksStr) ? Integer.parseInt(marksStr.trim()) : 1;
                } catch (NumberFormatException nfe) {
                    rowErrors.add("Row " + rowNum + ": invalid marks value \"" + marksStr + "\" - skipped.");
                    continue;
                }

                String validationError = validateQuestionFields(questionText, questionType, optionA,
                        optionB, optionC, optionD, correctOption, marks);
                if (validationError != null) {
                    rowErrors.add("Row " + rowNum + ": " + validationError + " - skipped.");
                    continue;
                }

                validRows.add(buildQuestion(examId, questionText, questionType.toUpperCase(),
                        optionA, optionB, optionC, optionD, correctOption.toUpperCase(),
                        marks, category, difficulty != null ? difficulty.toUpperCase() : "MEDIUM"));
            }
        } catch (IOException e) {
            return new BulkUploadResult(false, "Failed to read the uploaded file.", 0, rowErrors);
        }

        if (validRows.isEmpty()) {
            return new BulkUploadResult(false,
                    "No valid questions found in the uploaded file.", 0, rowErrors);
        }

        try {
            int inserted = questionDAO.insertBatch(validRows);
            activityLogDAO.log(actingUserId, "ADMIN_BULK_UPLOAD_QUESTIONS",
                    "Bulk-uploaded " + inserted + " questions to exam #" + examId, ip);
            String msg = inserted + " question(s) uploaded successfully." +
                    (rowErrors.isEmpty() ? "" : " " + rowErrors.size() + " row(s) were skipped - see details below.");
            return new BulkUploadResult(true, msg, inserted, rowErrors);
        } catch (SQLException e) {
            return new BulkUploadResult(false, "A database error occurred during bulk upload.", 0, rowErrors);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Question buildQuestion(long examId, String questionText, String questionType, String optionA,
                                    String optionB, String optionC, String optionD, String correctOption,
                                    int marks, String category, String difficulty) {
        Question q = new Question();
        q.setExamId(examId);
        q.setQuestionText(questionText.trim());
        q.setQuestionType(questionType);
        q.setOptionA(optionA);
        q.setOptionB(optionB);
        q.setOptionC(optionC);
        q.setOptionD(optionD);
        q.setCorrectOption(correctOption);
        q.setMarks(marks);
        q.setCategory(category);
        q.setDifficulty(VALID_DIFFICULTY.contains(difficulty) ? difficulty : "MEDIUM");
        return q;
    }

    private String validateQuestionFields(String questionText, String questionType, String optionA,
                                           String optionB, String optionC, String optionD,
                                           String correctOption, int marks) {
        if (!ValidationUtil.isNotBlank(questionText)) {
            return "Question text is required.";
        }
        if (questionType == null || !VALID_TYPES.contains(questionType.toUpperCase())) {
            return "Question type must be one of: " + VALID_TYPES;
        }
        if (!ValidationUtil.isNotBlank(optionA) || !ValidationUtil.isNotBlank(optionB)) {
            return "At least options A and B are required.";
        }
        if (correctOption == null || !VALID_OPTIONS.contains(correctOption.toUpperCase())) {
            return "Correct option must be one of: A, B, C, D.";
        }
        if (marks <= 0) {
            return "Marks must be greater than zero.";
        }
        return null;
    }

    private String getCol(List<String> cols, List<String> headers, String name) {
        int idx = headers.indexOf(name);
        if (idx < 0 || idx >= cols.size()) {
            return null;
        }
        return cols.get(idx).trim();
    }

    /**
     * Minimal CSV line parser supporting comma-separated values with
     * optional double-quote escaping (handles commas embedded inside
     * quoted question text). Not a full RFC 4180 parser, but sufficient
     * for the question bank upload format.
     */
    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }
}
