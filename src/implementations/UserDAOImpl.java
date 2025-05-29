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
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, first_name, last_name, email, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFirstname());
            stmt.setString(5, user.getLastname());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.isIsActive());
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getInt("userId"), rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), 
                        rs.getString("isActive"), rs.getString("created_at"), rs.getString("updated_at"));
            }
            return user;
        } catch (SQLException ex) {
            return null;
        }
    }

    @Override
    public List<User> searchUser(String str) {
        String sql = "SELECT * FROM users WHERE username LIKE ? OR firstname LIKE ? OR lastname LIKE ? OR email LIKE ?";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + str + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("userId"), rs.getString("username"), 
                        rs.getString("password"), rs.getString("role"), rs.getString("firstname"), 
                        rs.getString("lastname"), rs.getString("email"), rs.getString("isActive"),
                        rs.getString("created_at"), rs.getString("updated_at"));
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
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("userId"), rs.getString("username"), 
                        rs.getString("password"), rs.getString("role"), rs.getString("firstname"),
                        rs.getString("lastname"), rs.getString("email"), rs.getString("isActive"),
                        rs.getString("created_at"), rs.getString("updated_at"));
                users.add(user);
            }
            return users;
        }catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, firstname = ?, lastname = ?, email = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFirstname());
            stmt.setString(5, user.getLastname());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.isIsActive());
            stmt.setInt(8, user.getUserId());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
