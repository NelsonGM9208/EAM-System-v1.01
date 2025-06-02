/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import database.DBConnection;
import interfaces.UserDAO;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import models.User;

/**
 *
 * @author NelsonJrLHerrera
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public Integer addUser(User user) {
    String sql = "INSERT INTO users (username, password, role, first_name, last_name, email, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getRole());
        stmt.setString(4, user.getFirstname());
        stmt.setString(5, user.getLastname());
        stmt.setString(6, user.getEmail());
        stmt.setString(7, user.getIsActive());

        int affectedRows = stmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the newly inserted user_id
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}

    @Override
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("gender"), rs.getString("email"),
                        rs.getString("is_active"), rs.getString("created_at"), rs.getString("updated_at"));
            }
            return user;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public User getUserByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("gender"), rs.getString("email"),
                        rs.getString("is_active"), rs.getString("created_at"), rs.getString("updated_at"));
            }
            return user;
        } catch (SQLException ex) {
            return null;
        }
    }

    @Override
    public List<User> searchUser(String str) {
        String sql = "SELECT * FROM users WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ?";
        List<User> users = new ArrayList<>();
        User user;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + str + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("gender"), rs.getString("email"),
                        rs.getString("is_active"), rs.getString("created_at"), rs.getString("updated_at"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        User user;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("gender"), rs.getString("email"),
                        rs.getString("is_active"), rs.getString("created_at"), rs.getString("updated_at"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, first_name = ?, last_name = ?, email = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFirstname());
            stmt.setString(5, user.getLastname());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getIsActive());
            stmt.setInt(8, user.getUserId());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /*
    public Integer getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                return null; // Username not found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }*/
}
