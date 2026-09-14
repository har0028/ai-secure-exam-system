package com.aiexam.controller;

import com.aiexam.dao.NotificationDAO;
import com.aiexam.model.Notification;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "StudentNotificationsServlet", urlPatterns = {"/student/notifications"})
public class StudentNotificationsServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        try {
            List<Notification> notifications = notificationDAO.findByUserId(userId);
            req.setAttribute("notifications", notifications);
            notificationDAO.markAllRead(userId);
        } catch (SQLException e) {
            req.setAttribute("notifications", Collections.emptyList());
        }
        req.setAttribute("activePage", "notifications");
        req.getRequestDispatcher("/student/notifications.jsp").forward(req, resp);
    }
}
