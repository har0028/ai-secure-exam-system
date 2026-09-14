package com.aiexam.controller;

import com.aiexam.service.AuthService;
import com.aiexam.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String rollNumber = req.getParameter("rollNumber");

        AuthService.AuthResult result = authService.registerStudent(
                fullName, email, password, confirmPassword, rollNumber, req.getRemoteAddr());

        if (result.success) {
            req.setAttribute("successMessage", result.message);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("errorMessage", result.message);
            // Echo back safe values so the user does not have to retype everything
            req.setAttribute("fullName", ValidationUtil.sanitize(fullName));
            req.setAttribute("email", ValidationUtil.sanitize(email));
            req.setAttribute("rollNumber", ValidationUtil.sanitize(rollNumber));
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
