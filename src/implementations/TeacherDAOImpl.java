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
    public Integer createTeacher(Teacher teacher) {
        String sql = "INSERT INTO teachers (user_id, advisory_class) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getAdvisoryClass());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating teacher failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated teacher_id
                } else {
                    throw new SQLException("Creating teacher failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Teacher> getAllTeachers() {
        String sql = "SELECT * FROM teachers";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getInt("user_id"),
                        rs.getString("advisory_class"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                teachers.add(teacher);
            }
            return teachers;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Teacher getTeacherById(int teacherId) {
        Teacher teacher = null;
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                teacher = new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getInt("user_id"),
                        rs.getString("advisory_class"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }
            return teacher;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Teacher> searchTeachers(String str) {
        String sql = "SELECT * FROM teachers WHERE specialization LIKE ? OR advisory_class LIKE ?";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String search = "%" + str + "%";
            stmt.setString(1, search);
            stmt.setString(2, search);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getInt("user_id"),
                        rs.getString("advisory_class"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                teachers.add(teacher);
            }
            return teachers;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateTeacher(Teacher teacher) {
        String sql = "UPDATE teachers SET user_id = ?, specialization = ?, advisory_class = ? WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(3, teacher.getAdvisoryClass());
            stmt.setInt(4, teacher.getTeacherId());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteTeacher(int teacherId) {
        String sql = "DELETE FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public Teacher getTeacherByUserId(int userId) {
        Teacher teacher = null;
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                teacher = new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getInt("user_id"),
                        rs.getString("advisory_class"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }
            return teacher;
        } catch (SQLException e) {
            return null;
        }
    }
}
