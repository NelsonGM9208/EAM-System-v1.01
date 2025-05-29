/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import database.DBConnection;
import java.sql.*;
import models.User;
import util.HashUtil;

/**
 *
 * @author NelsonJrLHerrera
 */
public class Authentication {

    public User checkLogin(String username, String password){
        String hashedPassword = HashUtil.sha256(password);
        User user = null;
        
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("role"), rs.getString("first_name"), rs.getString("last_name"), 
                        rs.getString("email"), rs.getString("is_active"), "", "");              
            }
            return user;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
