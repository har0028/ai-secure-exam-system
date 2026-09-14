package com.aiexam.controller;

import com.aiexam.model.Result;
import com.aiexam.service.ResultService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminResultsServlet", urlPatterns = {"/admin/results"})
public class AdminResultsServlet extends HttpServlet {

    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("q");
        try {
            List<Result> results = resultService.search(keyword);
            req.setAttribute("results", results);
            req.setAttribute("searchKeyword", keyword);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Could not load results.");
        }
        req.setAttribute("activePage", "results");
        req.getRequestDispatcher("/admin/results.jsp").forward(req, resp);
    }
}
