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
import java.sql.Date;
import java.sql.SQLException;

/**
 * GET  /admin/students/create  -> blank form
 * GET  /admin/students/edit?id= -> pre-filled form
 * POST /admin/students/save    -> create or update depending on hidden "studentId" field
 */
@WebServlet(name = "AdminStudentFormServlet", urlPatterns = {
        "/admin/students/create", "/admin/students/edit", "/admin/students/save"})
public class AdminStudentFormServlet extends HttpServlet {

    private final StudentManagementService studentService = new StudentManagementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.endsWith("/edit")) {
            try {
                long studentId = Long.parseLong(req.getParameter("id"));
                Student student = studentService.getById(studentId);
                if (student == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/students");
                    return;
                }
                req.setAttribute("student", student);
            } catch (NumberFormatException | SQLException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/students");
                return;
            }
        }
        req.setAttribute("activePage", "students");
        req.getRequestDispatcher("/admin/student-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        long actingAdminUserId = (Long) session.getAttribute("userId");
        String ip = req.getRemoteAddr();

        String studentIdParam = req.getParameter("studentId");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String rollNumber = req.getParameter("rollNumber");
        String course = req.getParameter("course");
        String department = req.getParameter("department");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String dobParam = req.getParameter("dateOfBirth");

        boolean isEdit = studentIdParam != null && !studentIdParam.isEmpty();

        StudentManagementService.Result result;
        if (isEdit) {
            long studentId = Long.parseLong(studentIdParam);
            Date dob = (dobParam != null && !dobParam.isEmpty()) ? Date.valueOf(dobParam) : null;
            result = studentService.updateStudent(studentId, fullName, rollNumber, course,
                    department, phone, address, dob, actingAdminUserId, ip);
        } else {
            result = studentService.createStudent(fullName, email, password, rollNumber,
                    course, department, phone, actingAdminUserId, ip);
        }

        if (result.success) {
            req.getSession().setAttribute("flashSuccess", result.message);
            resp.sendRedirect(req.getContextPath() + "/admin/students");
        } else {
            req.setAttribute("errorMessage", result.message);
            req.setAttribute("activePage", "students");
            if (isEdit) {
                try {
                    req.setAttribute("student", studentService.getById(Long.parseLong(studentIdParam)));
                } catch (SQLException ignored) {
                }
            }
            req.getRequestDispatcher("/admin/student-form.jsp").forward(req, resp);
        }
    }
}
