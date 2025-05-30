/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NelsonJrLHerrera
 */
public class Teacher {

    //Attributes
    private int teacherId;
    private int userId;
    private String advisoryClass;
    private  String created_at;
    private  String updated_at;

    //Constructor
    public Teacher(int teacherId, int userId, String advisoryClass, String created_at, String updated_at) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.advisoryClass = advisoryClass;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
    
    //Created At and Updated At
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
    
    //Getters
    public int getTeacherId() {
        return teacherId;
    }

    public int getUserId() {
        return userId;
    }

    public String getAdvisoryClass() {
        return advisoryClass;
    }

    //Setters
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAdvisoryClass(String advisoryClass) {
        this.advisoryClass = advisoryClass;
    }
}
