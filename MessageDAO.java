package com.petadoption.dao;

import com.petadoption.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private String jdbcURL = "jdbc:h2:~/petadoption;DB_CLOSE_DELAY=-1";
    private String jdbcUsername = "sa";
    private String jdbcPassword = "";

    public MessageDAO() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public void sendMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, pet_id, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, message.getSenderId());
            pstmt.setInt(2, message.getReceiverId());
            pstmt.setInt(3, message.getPetId());
            pstmt.setString(4, message.getContent());
            pstmt.executeUpdate();
        }
    }

    public List<Message> getInbox(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.username as sender_username, p.name as pet_name " +
                     "FROM messages m " +
                     "JOIN users u ON m.sender_id = u.id " +
                     "JOIN pets p ON m.pet_id = p.id " +
                     "WHERE m.receiver_id = ? " +
                     "ORDER BY m.sent_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getInt("id"));
                msg.setSenderId(rs.getInt("sender_id"));
                msg.setReceiverId(rs.getInt("receiver_id"));
                msg.setPetId(rs.getInt("pet_id"));
                msg.setContent(rs.getString("content"));
                msg.setSentAt(rs.getTimestamp("sent_at"));
                msg.setSenderUsername(rs.getString("sender_username"));
                msg.setPetName(rs.getString("pet_name"));
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
