/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import models.User;
import java.util.List;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface UserDAO {

    public Integer addUser(User user);
    
    public User getUserById(int userId);
    
    public User getUserByUsername(String username);

    public List<User> searchUser(String str);

    public List<User> getAllUsers();

    public boolean updateUser(User user);

    public boolean deleteUser(int userId);
}
