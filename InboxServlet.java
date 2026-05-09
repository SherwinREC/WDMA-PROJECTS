package com.petadoption.servlet;

import com.petadoption.dao.MessageDAO;
import com.petadoption.model.Message;
import com.petadoption.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "InboxServlet", urlPatterns = {"/inbox"})
public class InboxServlet extends HttpServlet {
    private MessageDAO messageDAO;

    @Override
    public void init() {
        messageDAO = new MessageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Message> messages = messageDAO.getInbox(user.getId());
        request.setAttribute("messages", messages);
        request.getRequestDispatcher("/inbox.jsp").forward(request, response);
    }
}
