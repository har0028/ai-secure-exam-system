package com.aiexam.service;

import com.aiexam.dao.ActivityLogDAO;
import com.aiexam.dao.AdminDAO;
import com.aiexam.dao.StudentDAO;
import com.aiexam.dao.UserDAO;
import com.aiexam.model.User;
import com.aiexam.util.PasswordUtil;
import com.aiexam.util.ValidationUtil;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Service layer for authentication. Controllers (Servlets) call into this
 * class rather than touching DAOs directly, keeping business rules (password
 * policy, duplicate-email checks, audit logging) in one place.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
    }

    /**
     * Registers a new student account. Admin accounts are seeded directly
     * via the database and are not self-service registerable for security.
     */
    public AuthResult registerStudent(String fullName, String email, String password,
                                       String confirmPassword, String rollNumber, String ip) {
        try {
            if (!ValidationUtil.isNotBlank(fullName) || !ValidationUtil.isNotBlank(rollNumber)) {
                return new AuthResult(false, "Full name and roll number are required.", null);
            }
            if (!ValidationUtil.isValidEmail(email)) {
                return new AuthResult(false, "Please enter a valid email address.", null);
            }
            if (!password.equals(confirmPassword)) {
                return new AuthResult(false, "Passwords do not match.", null);
            }
            if (!ValidationUtil.isStrongPassword(password)) {
                return new AuthResult(false,
                        "Password must be at least 8 characters and include an uppercase letter, " +
                        "a lowercase letter, a digit, and a special character.", null);
            }
            if (userDAO.emailExists(email)) {
                return new AuthResult(false, "An account with this email already exists.", null);
            }

            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setRole("STUDENT");

            long userId = userDAO.insert(user);
            if (userId <= 0) {
                return new AuthResult(false, "Registration failed. Please try again.", null);
            }
            studentDAO.insert(userId, rollNumber.trim());
            activityLogDAO.log(userId, "REGISTER", "New student account created", ip);

            user.setUserId(userId);
            return new AuthResult(true, "Account created successfully. You can now log in.", user);

        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthResult(false, "A database error occurred during registration: " + e.getMessage(), null);
        }
    }

    /**
     * Authenticates a user by email/password and verifies their account is active.
     */
    public AuthResult login(String email, String password, String ip) {
        try {
            if (!ValidationUtil.isNotBlank(email) || !ValidationUtil.isNotBlank(password)) {
                return new AuthResult(false, "Email and password are required.", null);
            }

            User user = userDAO.findByEmail(email.trim().toLowerCase());
            if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                activityLogDAO.log(user != null ? user.getUserId() : null,
                        "LOGIN_FAILED", "Invalid credentials for email: " + email, ip);
                return new AuthResult(false, "Invalid email or password.", null);
            }

            if ("LOCKED".equals(user.getStatus())) {
                return new AuthResult(false, "This account has been locked. Contact the administrator.", null);
            }
            if ("INACTIVE".equals(user.getStatus())) {
                return new AuthResult(false, "This account is inactive. Contact the administrator.", null);
            }

            activityLogDAO.log(user.getUserId(), "LOGIN_SUCCESS", "User logged in", ip);
            return new AuthResult(true, "Login successful.", user);

        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthResult(false, "A database error occurred during login: " + e.getMessage(), null);
        }
    }

    public void logout(long userId, String ip) {
        activityLogDAO.log(userId, "LOGOUT", "User logged out", ip);
    }

    /**
     * Generates a single-use reset token valid for 30 minutes. In production
     * this token is emailed to the user; for this project it is returned so
     * it can be displayed/wired to an email service in a later phase.
     */
    public AuthResult initiatePasswordReset(String email, String ip) {
        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase());
            if (user == null) {
                // Do not reveal whether the email exists, to avoid account enumeration
                return new AuthResult(true,
                        "If an account exists with that email, a reset link has been generated.", null);
            }
            String token = UUID.randomUUID().toString();
            Timestamp expiry = new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000);
            userDAO.setResetToken(user.getUserId(), token, expiry);
            activityLogDAO.log(user.getUserId(), "PASSWORD_RESET_REQUESTED", "Reset token generated", ip);

            user.setResetToken(token);
            return new AuthResult(true,
                    "If an account exists with that email, a reset link has been generated.", user);
        } catch (SQLException e) {
            return new AuthResult(false, "A database error occurred.", null);
        }
    }

    public AuthResult completePasswordReset(String token, String newPassword, String confirmPassword, String ip) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                return new AuthResult(false, "Passwords do not match.", null);
            }
            if (!ValidationUtil.isStrongPassword(newPassword)) {
                return new AuthResult(false,
                        "Password must be at least 8 characters and include an uppercase letter, " +
                        "a lowercase letter, a digit, and a special character.", null);
            }
            User user = userDAO.findByResetToken(token);
            if (user == null) {
                return new AuthResult(false, "This reset link is invalid or has expired.", null);
            }
            userDAO.updatePassword(user.getUserId(), PasswordUtil.hash(newPassword));
            activityLogDAO.log(user.getUserId(), "PASSWORD_RESET_COMPLETED", "Password updated via reset link", ip);
            return new AuthResult(true, "Password updated successfully. You can now log in.", user);
        } catch (SQLException e) {
            return new AuthResult(false, "A database error occurred.", null);
        }
    }
}
