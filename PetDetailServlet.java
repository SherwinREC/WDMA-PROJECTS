package com.petadoption.servlet;

import com.petadoption.dao.PetDAO;
import com.petadoption.model.Pet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.petadoption.model.User;
import java.io.IOException;

@WebServlet(name = "PetDetailServlet", urlPatterns = {"/pet-details"})
public class PetDetailServlet extends HttpServlet {
    private PetDAO petDAO;

    @Override
    public void init() {
        petDAO = new PetDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer userId = (user != null) ? user.getId() : null;

        String idStr = request.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            Pet pet = petDAO.selectPet(id, userId);
            if (pet != null) {
                request.setAttribute("pet", pet);
                request.getRequestDispatcher("/pet-detail.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/pets");
    }
}
