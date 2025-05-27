/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import database.DBConnection;
import interfaces.StudentDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Student;

/**
 *
 * @author NelsonJrLHerrera
 */
public class StudentDAOImpl implements StudentDAO {

    @Override
    public void create(Student student) throws SQLException {
        String query = "INSERT INTO students(student_id, user_id, lrn, gradeLevel, section, adviser_id, photoPath) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, student.getStudent_id());
            pstmt.setInt(2, student.getUser_id());
            pstmt.setLong(3, student.getLrn());
            pstmt.setInt(4, student.getGradeLevel());
            pstmt.setString(5, student.getSection());
            pstmt.setInt(6, student.getAdviser_id());
            pstmt.setString(7, student.getPhotoPath());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Student read_one(int student_id) throws SQLException {
        Student student = null;
        String query = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, student_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                student = new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getLong("lrn"),
                        rs.getInt("gradeLevel"),
                        rs.getString("section"),
                        rs.getInt("adviser_id"),
                        rs.getString("photoPath")
                );
            }
        }
        return student;
    }

    @Override
    public List<Student> read_all() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE status LIKE ? ORDER BY student_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "Completed");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getLong("lrn"),
                        rs.getInt("gradeLevel"),
                        rs.getString("section"),
                        rs.getInt("adviser_id"),
                        rs.getString("photoPath")
                ));
            }
        }
        return students;
    }

    @Override
    public void update(int student_id, Student student) throws SQLException {
        String query = "UPDATE students SET lrn = ?, gradeLevel = ?, section = ?, adviser_id = ?, photoPath = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, student.getLrn());
            pstmt.setInt(2, student.getGradeLevel());
            pstmt.setString(3, student.getSection());
            pstmt.setInt(4, student.getAdviser_id());
            pstmt.setString(5, student.getPhotoPath());
            pstmt.setInt(6, student_id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Student> readStudentsByUser(int user_id) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE user_id = ? AND status LIKE ? ORDER BY student_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, user_id);
            pstmt.setString(2, "Completed");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getLong("lrn"),
                        rs.getInt("gradeLevel"),
                        rs.getString("section"),
                        rs.getInt("adviser_id"),
                        rs.getString("photoPath")
                ));
            }
        }
        return students;
    }
}
