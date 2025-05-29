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
    private int userId;
    private   String username;
    private String password;
    private String role;
    private String firstname;
    private String lastname;
    private String email;
    private String isActive;
    private String created_at;
    private String updated_at;

    //Constructor
    public User(int userId, String username, String password, String role, String firstname, String lastname, String email, String isActive, String created_at, String updated_at) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.isActive = isActive;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
    

    //getters and setters (NON-STATIC)
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    
    public void setUpdated_at(String updated_at) {    
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
    
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String isIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
