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
    private String specialization;
    private String advisoryClass;

    //Constructor
    public Teacher(int teacherId, int userId, String specialization, String advisoryClass) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.specialization = specialization;
        this.advisoryClass = advisoryClass;
    }

    //Getters
    public int getTeacherId() {
        return teacherId;
    }

    public int getUserId() {
        return userId;
    }

    public String getSpecialization() {
        return specialization;
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

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setAdvisoryClass(String advisoryClass) {
        this.advisoryClass = advisoryClass;
    }
}
