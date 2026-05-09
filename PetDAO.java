package com.petadoption.dao;

import com.petadoption.model.Pet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class PetDAO {
    private String jdbcURL = "jdbc:h2:~/petadoption;DB_CLOSE_DELAY=-1";
    private String jdbcUsername = "sa";
    private String jdbcPassword = "";

    public PetDAO() {
        try {
            Class.forName("org.h2.Driver");
            initDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void initDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            InputStream is = getClass().getClassLoader().getResourceAsStream("init.sql");
            if (is != null) {
                String sqlContent = new BufferedReader(new InputStreamReader(is))
                        .lines().collect(Collectors.joining("\n"));
                String[] sqlStatements = sqlContent.split(";");
                for (String sql : sqlStatements) {
                    if (!sql.trim().isEmpty()) {
                        try {
                            statement.execute(sql.trim());
                        } catch (SQLException e) {
                            // Ignore errors for "ALREADY EXISTS" or "IF NOT EXISTS" related failures if they happen
                            if (!e.getMessage().contains("already exists")) {
                                System.err.println("Error executing SQL: " + sql);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPet(Pet pet) throws SQLException {
        String sql = "INSERT INTO pets (name, species, breed, age, description, owner_contact, image_url, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, pet.getName());
            preparedStatement.setString(2, pet.getSpecies());
            preparedStatement.setString(3, pet.getBreed());
            preparedStatement.setInt(4, pet.getAge());
            preparedStatement.setString(5, pet.getDescription());
            preparedStatement.setString(6, pet.getOwnerContact());
            preparedStatement.setString(7, pet.getImageUrl());
            if (pet.getUserId() != null) {
                preparedStatement.setInt(8, pet.getUserId());
            } else {
                preparedStatement.setNull(8, Types.INTEGER);
            }
            preparedStatement.executeUpdate();
        }
    }

    public void updatePet(Pet pet) throws SQLException {
        String sql = "UPDATE pets SET name = ?, species = ?, breed = ?, age = ?, description = ?, owner_contact = ?, image_url = ? WHERE id = ? AND user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, pet.getName());
            preparedStatement.setString(2, pet.getSpecies());
            preparedStatement.setString(3, pet.getBreed());
            preparedStatement.setInt(4, pet.getAge());
            preparedStatement.setString(5, pet.getDescription());
            preparedStatement.setString(6, pet.getOwnerContact());
            preparedStatement.setString(7, pet.getImageUrl());
            preparedStatement.setInt(8, pet.getId());
            preparedStatement.setInt(9, pet.getUserId());
            preparedStatement.executeUpdate();
        }
    }

    public Pet selectPet(int id, Integer currentUserId) {
        Pet pet = null;
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM pet_likes WHERE pet_id = p.id) as likes_count, " +
                     "(CASE WHEN ? IS NULL THEN 0 ELSE (SELECT COUNT(*) FROM pet_likes WHERE pet_id = p.id AND user_id = ?) END) as is_liked " +
                     "FROM pets p WHERE p.id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (currentUserId == null) {
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(1, currentUserId);
                preparedStatement.setInt(2, currentUserId);
            }
            preparedStatement.setInt(3, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                pet = mapResultSetToPet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pet;
    }

    public List<Pet> selectAllPets(Integer currentUserId) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM pet_likes WHERE pet_id = p.id) as likes_count, " +
                     "(CASE WHEN ? IS NULL THEN 0 ELSE (SELECT COUNT(*) FROM pet_likes WHERE pet_id = p.id AND user_id = ?) END) as is_liked " +
                     "FROM pets p " +
                     "ORDER BY likes_count DESC, p.created_at DESC";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (currentUserId == null) {
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(1, currentUserId);
                preparedStatement.setInt(2, currentUserId);
            }
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                pets.add(mapResultSetToPet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    public void toggleLike(int userId, int petId) throws SQLException {
        String checkSql = "SELECT 1 FROM pet_likes WHERE user_id = ? AND pet_id = ?";
        try (Connection conn = getConnection()) {
            boolean exists = false;
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, petId);
                ResultSet rs = pstmt.executeQuery();
                exists = rs.next();
            }

            if (exists) {
                String deleteSql = "DELETE FROM pet_likes WHERE user_id = ? AND pet_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, petId);
                    pstmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO pet_likes (user_id, pet_id) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, petId);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    public List<Pet> searchPets(String query, Integer currentUserId) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM pet_likes WHERE pet_id = p.id) as likes_count, " +
                     "(CASE WHEN ? IS NULL THEN 0 ELSE (SELECT COUNT(*) FROM pet_likes WHERE pet_id = p.id AND user_id = ?) END) as is_liked " +
                     "FROM pets p " +
                     "WHERE LOWER(p.name) LIKE LOWER(?) OR LOWER(p.species) LIKE LOWER(?) OR LOWER(p.breed) LIKE LOWER(?) " +
                     "ORDER BY likes_count DESC, p.created_at DESC";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (currentUserId == null) {
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(1, currentUserId);
                preparedStatement.setInt(2, currentUserId);
            }
            String searchPattern = "%" + query + "%";
            preparedStatement.setString(3, searchPattern);
            preparedStatement.setString(4, searchPattern);
            preparedStatement.setString(5, searchPattern);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                pets.add(mapResultSetToPet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    private Pet mapResultSetToPet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getInt("id"));
        pet.setName(rs.getString("name"));
        pet.setSpecies(rs.getString("species"));
        pet.setBreed(rs.getString("breed"));
        pet.setAge(rs.getInt("age"));
        pet.setDescription(rs.getString("description"));
        pet.setOwnerContact(rs.getString("owner_contact"));
        pet.setImageUrl(rs.getString("image_url"));
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            pet.setUserId(userId);
        }
        pet.setLikesCount(rs.getInt("likes_count"));
        pet.setLikedByUser(rs.getInt("is_liked") > 0);
        pet.setCreatedAt(rs.getTimestamp("created_at"));
        return pet;
    }
}
