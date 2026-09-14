package com.aiexam.controller;

import com.aiexam.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        AuthService.AuthResult result = authService.initiatePasswordReset(email, req.getRemoteAddr());

        // NOTE: In production the reset link below is emailed to the user via an
        // SMTP/email service (planned for a later phase) rather than shown on screen.
        if (result.user != null && result.user.getResetToken() != null) {
            String resetLink = req.getContextPath() + "/reset-password?token=" + result.user.getResetToken();
            req.setAttribute("devResetLink", resetLink);
        }
        req.setAttribute("successMessage", result.message);
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }
}
