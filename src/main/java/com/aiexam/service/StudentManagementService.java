package com.aiexam.service;

import com.aiexam.dao.ActivityLogDAO;
import com.aiexam.dao.StudentDAO;
import com.aiexam.dao.UserDAO;
import com.aiexam.model.Student;
import com.aiexam.model.User;
import com.aiexam.util.PasswordUtil;
import com.aiexam.util.ValidationUtil;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin-side student management: create, update, delete, search.
 * Distinct from AuthService.registerStudent, which is the self-service
 * student registration flow - this one is admin-initiated and lets the
 * admin set an initial password directly rather than going through the
 * public registration form.
 */
public class StudentManagementService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    public static class Result {
        public final boolean success;
        public final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public Result createStudent(String fullName, String email, String password, String rollNumber,
                                 String course, String department, String phone,
                                 long actingAdminUserId, String ip) {
        try {
            if (!ValidationUtil.isNotBlank(fullName) || !ValidationUtil.isNotBlank(rollNumber)) {
                return new Result(false, "Full name and roll number are required.");
            }
            if (!ValidationUtil.isValidEmail(email)) {
                return new Result(false, "Please enter a valid email address.");
            }
            if (!ValidationUtil.isStrongPassword(password)) {
                return new Result(false,
                        "Password must be at least 8 characters and include an uppercase letter, " +
                        "a lowercase letter, a digit, and a special character.");
            }
            if (userDAO.emailExists(email)) {
                return new Result(false, "An account with this email already exists.");
            }

            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setRole("STUDENT");

            long userId = userDAO.insert(user);
            if (userId <= 0) {
                return new Result(false, "Failed to create student account.");
            }
            studentDAO.insert(userId, rollNumber.trim());

            // Fill in the optional profile fields via update
            Student s = studentDAO.findByUserId(userId);
            s.setCourse(course);
            s.setDepartment(department);
            s.setPhone(phone);
            studentDAO.update(s);

            activityLogDAO.log(actingAdminUserId, "ADMIN_CREATE_STUDENT",
                    "Created student account: " + email, ip);
            return new Result(true, "Student account created successfully.");

        } catch (SQLException e) {
            return new Result(false, "A database error occurred while creating the student.");
        }
    }

    public Result updateStudent(long studentId, String fullName, String rollNumber, String course,
                                 String department, String phone, String address, Date dateOfBirth,
                                 long actingAdminUserId, String ip) {
        try {
            Student s = studentDAO.findById(studentId);
            if (s == null) {
                return new Result(false, "Student not found.");
            }
            if (!ValidationUtil.isNotBlank(fullName) || !ValidationUtil.isNotBlank(rollNumber)) {
                return new Result(false, "Full name and roll number are required.");
            }

            userDAO.updateFullName(s.getUserId(), fullName.trim());

            s.setRollNumber(rollNumber.trim());
            s.setCourse(course);
            s.setDepartment(department);
            s.setPhone(phone);
            s.setAddress(address);
            s.setDateOfBirth(dateOfBirth);
            studentDAO.update(s);

            activityLogDAO.log(actingAdminUserId, "ADMIN_UPDATE_STUDENT",
                    "Updated student profile: " + s.getEmail(), ip);
            return new Result(true, "Student profile updated successfully.");

        } catch (SQLException e) {
            return new Result(false, "A database error occurred while updating the student.");
        }
    }

    public Result deleteStudent(long studentId, long actingAdminUserId, String ip) {
        try {
            Student s = studentDAO.findById(studentId);
            if (s == null) {
                return new Result(false, "Student not found.");
            }
            // Deleting the user row cascades to students, exam_attempts, etc. via FK ON DELETE CASCADE
            boolean deleted = userDAO.delete(s.getUserId());
            if (!deleted) {
                return new Result(false, "Failed to delete student.");
            }
            activityLogDAO.log(actingAdminUserId, "ADMIN_DELETE_STUDENT",
                    "Deleted student account: " + s.getEmail(), ip);
            return new Result(true, "Student deleted successfully.");

        } catch (SQLException e) {
            return new Result(false, "A database error occurred while deleting the student.");
        }
    }

    public List<Student> listAll() throws SQLException {
        return studentDAO.findAll();
    }

    public List<Student> search(String keyword) throws SQLException {
        if (!ValidationUtil.isNotBlank(keyword)) {
            return studentDAO.findAll();
        }
        return studentDAO.search(keyword.trim());
    }

    public Student getById(long studentId) throws SQLException {
        return studentDAO.findById(studentId);
    }
}
