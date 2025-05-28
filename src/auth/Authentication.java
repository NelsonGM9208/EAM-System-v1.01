/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import database.DBConnection;
import java.sql.*;
import javax.swing.JOptionPane;
import mainGUIs.LoginGUI;
import models.User;
import util.HashUtil;

/**
 *
 * @author NelsonJrLHerrera
 */
public class Authentication {

    public static User checkLogin(String username, String password){
        String hashedPassword = HashUtil.sha256(password);
        LoginGUI loginGUI = new LoginGUI();
        
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("userId"), rs.getString("username"), rs.getString("password"), rs.getString("role"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getBoolean("isActive"));
                return user;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
