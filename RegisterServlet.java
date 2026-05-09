package com.petadoption.servlet;

import com.petadoption.dao.UserDAO;
import com.petadoption.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        User user = new User(username, password, email);
        try {
            if (userDAO.registerUser(user)) {
                response.sendRedirect(request.getContextPath() + "/login?registered=true");
            } else {
                request.setAttribute("error", "Username already exists or registration failed.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
