/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import database.DBConnection;
import interfaces.TeacherDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Teacher;

/**
 *
 * @author NelsonJrLHerrera
 */
public class TeacherDAOImpl implements TeacherDAO {

    @Override
    public void createTeacher(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teachers (teacher_id, user_id, specialization, advisory_class) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getTeacherId());
            stmt.setInt(2, teacher.getUserId());
            stmt.setString(3, teacher.getSpecialization());
            stmt.setString(4, teacher.getAdvisoryClass());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Teacher> getAllTeachers() throws SQLException {
        String sql = "SELECT * FROM teachers";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getInt("user_id"),
                        rs.getString("specialization"),
                        rs.getString("advisory_class")
                );
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    @Override
    public Teacher getTeacherById(String teacherId) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(teacherId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(
                            rs.getInt("teacher_id"),
                            rs.getInt("user_id"),
                            rs.getString("specialization"),
                            rs.getString("advisory_class")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Teacher> searchTeachers(String keyword) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE specialization LIKE ? OR advisory_class LIKE ?";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Teacher teacher = new Teacher(
                            rs.getInt("teacher_id"),
                            rs.getInt("user_id"),
                            rs.getString("specialization"),
                            rs.getString("advisory_class")
                    );
                    teachers.add(teacher);
                }
            }
        }
        return teachers;
    }

    @Override
    public void updateTeacher(Teacher teacher) throws SQLException {
        String sql = "UPDATE teachers SET user_id = ?, specialization = ?, advisory_class = ? WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getSpecialization());
            stmt.setString(3, teacher.getAdvisoryClass());
            stmt.setInt(4, teacher.getTeacherId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteTeacher(String teacherId) throws SQLException {
        String sql = "DELETE FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(teacherId));
            stmt.executeUpdate();
        }
    }
}
