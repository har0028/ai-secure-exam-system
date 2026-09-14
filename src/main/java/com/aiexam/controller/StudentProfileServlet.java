package com.aiexam.controller;

import com.aiexam.dao.StudentDAO;
import com.aiexam.model.Student;
import com.aiexam.service.AuthService;
import com.aiexam.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "StudentProfileServlet", urlPatterns = {"/student/profile"})
public class StudentProfileServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        try {
            req.setAttribute("student", studentDAO.findByUserId(userId));
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Could not load profile.");
        }
        req.setAttribute("activePage", "profile");
        req.getRequestDispatcher("/student/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (ValidationUtil.isNotBlank(newPassword)) {
            if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("errorMessage", "Passwords do not match.");
            } else if (!ValidationUtil.isStrongPassword(newPassword)) {
                req.setAttribute("errorMessage",
                        "Password must be at least 8 characters with uppercase, lowercase, number, and special character.");
            } else {
                AuthService.AuthResult r = authService.completePasswordReset(null, newPassword, confirmPassword, req.getRemoteAddr());
                // Direct update path for logged-in users
                try {
                    com.aiexam.util.PasswordUtil pw = null;
                    new com.aiexam.dao.UserDAO().updatePassword(userId,
                            com.aiexam.util.PasswordUtil.hash(newPassword));
                    session.setAttribute("flashSuccess", "Password updated successfully.");
                } catch (SQLException e) {
                    req.setAttribute("errorMessage", "Failed to update password.");
                }
            }
        } else {
            session.setAttribute("flashSuccess", "Profile updated.");
        }

        try {
            req.setAttribute("student", studentDAO.findByUserId(userId));
        } catch (SQLException ignored) {}
        req.setAttribute("activePage", "profile");
        req.getRequestDispatcher("/student/profile.jsp").forward(req, resp);
    }
}
