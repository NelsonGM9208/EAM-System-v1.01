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
    public boolean create(Student student) {
        String query = "INSERT INTO students(lrn, grade_level, section, class_id, photo_path, user_id) "
                + "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, student.getLrn());
            pstmt.setInt(2, student.getGradeLevel());
            pstmt.setString(3, student.getSection());
            pstmt.setInt(4, student.getClass_id());
            pstmt.setString(5, student.getPhotoPath());
            pstmt.setInt(6, student.getUser_id());
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Student read_one(int student_id) {
        Student student = null;
        String query = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, student_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                student = new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getLong("lrn"),
                        rs.getInt("grade_level"),
                        rs.getString("section"),
                        rs.getInt("class_id"),
                        rs.getString("photo_path"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }
            return student;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Student> read_all() {
        List<Student> students = new ArrayList<>();
        Student student;
        String query = "SELECT * FROM students";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                student = new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getLong("lrn"),
                        rs.getInt("grade_level"),
                        rs.getString("section"),
                        rs.getInt("class_id"),
                        rs.getString("photo_path"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                students.add(student);
            }
            return students;
        } catch (SQLException e) {
            return null;
        }

    }

    @Override
    public boolean update(Student student) {
        String query = "UPDATE students SET lrn = ?, grade_level = ?, section = ?, class_id = ?, photo_path = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, student.getLrn());
            pstmt.setInt(2, student.getGradeLevel());
            pstmt.setString(3, student.getSection());
            pstmt.setInt(4, student.getClass_id());
            pstmt.setString(5, student.getPhotoPath());
            pstmt.setInt(6, student.getStudent_id());
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(int student_id) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student_id);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Student> read_all_by_class_id(int classId) {
        List<Student> students = new ArrayList<>();
        Student student;
        String query = "SELECT * FROM students WHERE class_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, classId); // set the class_id parameter
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                student = new Student(
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getLong("lrn"),
                        rs.getInt("grade_level"),
                        rs.getString("section"),
                        rs.getInt("class_id"),
                        rs.getString("photo_path"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                students.add(student);
            }

            return students;
        } catch (SQLException e) {
            e.printStackTrace(); // You may log this instead
            return null;
        }
    }

    public int getStudentIdByLRN(long lrn) {
        String query = "SELECT student_id FROM students WHERE lrn = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, lrn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("student_id");
            } else {
                return -1; // or throw an exception, or handle "not found" in your own way
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public Student getStudentByUserId(int userId) {
    String query = "SELECT * FROM students WHERE user_id = ?";
    try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new Student(
                rs.getInt("student_id"),
                rs.getInt("user_id"),
                rs.getLong("lrn"),
                rs.getInt("grade_level"),
                rs.getString("section"),
                rs.getInt("class_id"),
                rs.getString("photo_path"),
                rs.getString("created_at"),
                rs.getString("updated_at")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
}
