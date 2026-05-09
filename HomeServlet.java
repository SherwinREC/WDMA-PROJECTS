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
import java.util.stream.Collectors;

@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {
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

        List<Pet> allPets = petDAO.selectAllPets(userId);
        // Get only first 3 pets for featured section
        List<Pet> featuredPets = allPets.stream().limit(6).collect(Collectors.toList());
        request.setAttribute("featuredPets", featuredPets);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
