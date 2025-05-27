/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import models.User;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface UserDAO {

    public void addUser(User user) throws SQLException;

    public User getUserById(int userId) throws SQLException;

    public List<User> searchUser(String str) throws SQLException;

    public List<User> getAllUsers() throws SQLException;

    public boolean updateUser(User user) throws SQLException;

    public boolean deleteUser(int userId) throws SQLException;
}
