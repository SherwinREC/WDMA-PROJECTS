package com.petadoption.servlet;

import com.petadoption.dao.PetDAO;
import com.petadoption.model.Pet;
import com.petadoption.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet(name = "EditPetServlet", urlPatterns = {"/edit-pet"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class EditPetServlet extends HttpServlet {
    private PetDAO petDAO;
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void init() {
        petDAO = new PetDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        Pet pet = petDAO.selectPet(id, user.getId());

        if (pet == null || pet.getUserId() != user.getId()) {
            response.sendRedirect(request.getContextPath() + "/pets");
            return;
        }

        request.setAttribute("pet", pet);
        request.getRequestDispatcher("/edit-pet.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        Pet existingPet = petDAO.selectPet(id, user.getId());

        if (existingPet == null || existingPet.getUserId() != user.getId()) {
            response.sendRedirect(request.getContextPath() + "/pets");
            return;
        }

        String name = request.getParameter("name");
        String species = request.getParameter("species");
        String breed = request.getParameter("breed");
        int age = Integer.parseInt(request.getParameter("age"));
        String description = request.getParameter("description");
        String ownerContact = request.getParameter("ownerContact");

        // Handle File Upload (optional if user wants to keep current image)
        String imageUrl = existingPet.getImageUrl();
        Part filePart = request.getPart("petImage");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = UUID.randomUUID().toString() + "_" + getSafeFileName(filePart);
            String realPath = getServletContext().getRealPath("");
            if (realPath == null) realPath = System.getProperty("java.io.tmpdir");
            String uploadPath = realPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            filePart.write(uploadPath + File.separator + fileName);
            imageUrl = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
        }

        Pet updatedPet = new Pet(name, species, breed, age, description, ownerContact, imageUrl, user.getId());
        updatedPet.setId(id);

        try {
            petDAO.updatePet(updatedPet);
            response.sendRedirect(request.getContextPath() + "/pet-details?id=" + id + "&updated=true");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error updating pet: " + e.getMessage());
            request.setAttribute("pet", updatedPet);
            request.getRequestDispatcher("/edit-pet.jsp").forward(request, response);
        }
    }

    private String getSafeFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                String name = token.substring(token.indexOf("=") + 2, token.length() - 1);
                return new File(name).getName().replace("\"", "");
            }
        }
        return "unknown";
    }
}
