/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NelsonJrLHerrera
 */
public class Student {

    private static int student_id;
    private static int user_id;
    private static long lrn;
    private static int gradeLevel;
    private static String section;
    private static int adviser_id;
    private static String photoPath;

    //Constructor
    public Student(int student_id, int user_id, long lrn, int gradeLevel, String section, int adviser_id, String photoPath) {
        this.student_id = student_id;
        this.user_id = user_id;
        this.lrn = lrn;
        this.gradeLevel = gradeLevel;
        this.section = section;
        this.adviser_id = adviser_id;
        this.photoPath = photoPath;
    }

    //Getters
    public int getStudent_id() {
        return student_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public long getLrn() {
        return lrn;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public String getSection() {
        return section;
    }

    public int getAdviser_id() {
        return adviser_id;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    //Setters
    public void setStudent_id(int student_id) {
        Student.student_id = student_id;
    }

    public void setUser_id(int user_id) {
        Student.user_id = user_id;
    }

    public void setLrn(long lrn) {
        Student.lrn = lrn;
    }

    public void setGradeLevel(int gradeLevel) {
        Student.gradeLevel = gradeLevel;
    }

    public void setSection(String section) {
        Student.section = section;
    }

    public void setAdviser_id(int adviser_id) {
        Student.adviser_id = adviser_id;
    }

    public void setPhotoPath(String photoPath) {
        Student.photoPath = photoPath;
    }
}
