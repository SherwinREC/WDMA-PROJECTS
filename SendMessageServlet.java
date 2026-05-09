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
import java.sql.SQLException;

@WebServlet(name = "SendMessageServlet", urlPatterns = {"/send-message"})
public class SendMessageServlet extends HttpServlet {
    private MessageDAO messageDAO;

    @Override
    public void init() {
        messageDAO = new MessageDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User sender = (session != null) ? (User) session.getAttribute("user") : null;

        if (sender == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int receiverId = Integer.parseInt(request.getParameter("receiverId"));
        int petId = Integer.parseInt(request.getParameter("petId"));
        String content = request.getParameter("content");

        Message msg = new Message(sender.getId(), receiverId, petId, content);
        
        try {
            messageDAO.sendMessage(msg);
            response.sendRedirect(request.getContextPath() + "/pet-details?id=" + petId + "&msgSent=true");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/pet-details?id=" + petId + "&msgError=true");
        }
    }
}
