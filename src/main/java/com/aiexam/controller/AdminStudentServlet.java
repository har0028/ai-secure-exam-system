package com.aiexam.controller;

import com.aiexam.model.Student;
import com.aiexam.service.StudentManagementService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles the student list/search screen at GET /admin/students.
 * Create/edit/delete are handled by dedicated action servlets below in
 * this same file's sibling classes for clarity of single-responsibility.
 */
@WebServlet(name = "AdminStudentListServlet", urlPatterns = {"/admin/students"})
public class AdminStudentServlet extends HttpServlet {

    private final StudentManagementService studentService = new StudentManagementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("q");
        try {
            List<Student> students = studentService.search(keyword);
            req.setAttribute("students", students);
            req.setAttribute("searchKeyword", keyword);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Could not load students.");
        }
        req.setAttribute("activePage", "students");
        req.getRequestDispatcher("/admin/students.jsp").forward(req, resp);
    }
}
