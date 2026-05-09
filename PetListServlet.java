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
import java.util.List;

@WebServlet(name = "PetListServlet", urlPatterns = {"/pets"})
public class PetListServlet extends HttpServlet {
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

        String query = request.getParameter("search");
        List<Pet> pets;
        if (query != null && !query.trim().isEmpty()) {
            // For simplicity, search results also follow likes order if updated
            pets = petDAO.searchPets(query, userId);
            request.setAttribute("searchQuery", query);
        } else {
            pets = petDAO.selectAllPets(userId);
        }
        request.setAttribute("pets", pets);
        request.getRequestDispatcher("/pets.jsp").forward(request, response);
    }
}
