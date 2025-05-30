/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author HansBVitorillo
 */
public class Classes {
    private int class_id;
    private int adviser_id;
    private int grade; 
    private String section;
    private String created_at;
    private String updated_at;
    
    public Classes(int class_id, int adviser_id, int grade, String section, String created_at, String updated_at){
        this.class_id = class_id;
        this.adviser_id = adviser_id;
        this.grade = grade;
        this.section = section;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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
