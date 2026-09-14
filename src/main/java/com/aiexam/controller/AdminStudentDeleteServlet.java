package com.aiexam.controller;

import com.aiexam.service.StudentManagementService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminStudentDeleteServlet", urlPatterns = {"/admin/students/delete"})
public class AdminStudentDeleteServlet extends HttpServlet {

    private final StudentManagementService studentService = new StudentManagementService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long actingAdminUserId = (Long) session.getAttribute("userId");

        try {
            long studentId = Long.parseLong(req.getParameter("id"));
            StudentManagementService.Result result =
                    studentService.deleteStudent(studentId, actingAdminUserId, req.getRemoteAddr());
            session.setAttribute(result.success ? "flashSuccess" : "flashError", result.message);
        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid student reference.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/students");
    }
}
