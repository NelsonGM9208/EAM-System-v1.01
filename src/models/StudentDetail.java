/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NelsonJrLHerrera
 */
public class StudentDetail {
    private int userId;
    private long lrn;
    private String fullName;
    private String gender;
    private int gradeLevel;
    private String section;
    private String role;
    private String email;
    private String status;

    // Constructor
    public StudentDetail(int userId, long lrn, String fullName, String gender,
                         int gradeLevel, String section, String role, String email, String status) {
        this.userId = userId;
        this.lrn = lrn;
        this.fullName = fullName;
        this.gender = gender;
        this.gradeLevel = gradeLevel;
        this.section = section;
        this.role = role;
        this.email = email;
        this.status = status;
    }

    // Getters and setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getLrn() {
        return lrn;
    }

    public void setLrn(long lrn) {
        this.lrn = lrn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}