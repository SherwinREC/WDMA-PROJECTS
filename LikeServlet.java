package com.petadoption.servlet;

import com.petadoption.dao.PetDAO;
import com.petadoption.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "LikeServlet", urlPatterns = {"/like"})
public class LikeServlet extends HttpServlet {
    private PetDAO petDAO;

    @Override
    public void init() {
        petDAO = new PetDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int petId = Integer.parseInt(request.getParameter("petId"));
            petDAO.toggleLike(user.getId(), petId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
