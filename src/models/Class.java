/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author HansBVitorillo
 */
public class Class {
    private int class_id;
    private int adviser_id;
    private int grade; 
    private String section;
    
    public Class(int class_id, int adviser_id, int grade, String section){
        this.class_id = class_id;
        this.adviser_id = adviser_id;
        this.grade = grade;
        this.section = section;
    }

    public int getClass_id() {
        return class_id;
    }

    public int getAdviser_id() {
        return adviser_id;
    }

    public int getGrade() {
        return grade;
    }

    public String getSection() {
        return section;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public void setAdviser_id(int adviser_id) {
        this.adviser_id = adviser_id;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
