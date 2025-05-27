/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NelsonJrLHerrera
 */
public class User {
    //Attributes
    private static int userId;
    private static String username;
    private static String password;
    private static String role;
    private static String firstname;
    private static String lastname;
    private static String email;
    private static boolean isActive;

    //Constructor
    public User(int userId, String username, String password, String role, String firstname, String lastname, String email, boolean isActive){   
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.isActive = isActive;
    }

    //getters and setters (NON-STATIC)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        User.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        User.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        User.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        User.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        User.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        User.lastname = lastname;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        User.email = email;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        User.isActive = isActive;
    }
}
