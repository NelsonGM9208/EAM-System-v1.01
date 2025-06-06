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
import models.TeacherDetail;

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
    public List<TeacherDetail> searchTeachers(String query) {
    String sql = """
        SELECT u.user_id, u.username, u.first_name, u.last_name, u.gender,
               c.grade, c.section, u.role, u.email, u.is_active
        FROM teachers t
        JOIN users u ON t.user_id = u.user_id
        LEFT JOIN classes c ON t.teacher_id = c.adviser_id
        WHERE u.username LIKE ? 
           OR u.first_name LIKE ? 
           OR u.last_name LIKE ?
           OR CONCAT(c.grade, '-', c.section) LIKE ?
        ORDER BY u.last_name ASC, u.first_name ASC
    """;

    List<TeacherDetail> teachers = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        String searchTerm = "%" + query + "%";
        for (int i = 1; i <= 4; i++) {
            stmt.setString(i, searchTerm);
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int userId = rs.getInt("user_id");
            String username = rs.getString("username");
            String fullName = rs.getString("last_name") + ", " + rs.getString("first_name");
            String gender = rs.getString("gender");
            String advisory = rs.getString("grade") != null && rs.getString("section") != null
                    ? rs.getInt("grade") + "-" + rs.getString("section")
                    : "None";
            String role = rs.getString("role");
            String email = rs.getString("email");
            String status = rs.getString("is_active");

            teachers.add(new TeacherDetail(userId, username, fullName, gender, advisory, role, email, status));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return teachers;
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
    
    public List<TeacherDetail> sortTeachersByAdvisory(String advisory) {
    String sql = """
        SELECT u.user_id, u.username, u.first_name, u.last_name, u.gender,
               c.grade, c.section, u.role, u.email, u.is_active
        FROM teachers t
        JOIN users u ON t.user_id = u.user_id
        JOIN classes c ON t.teacher_id = c.adviser_id
        WHERE CONCAT(c.grade, '-', c.section) = ?
        ORDER BY u.last_name ASC, u.first_name ASC
    """;

    List<TeacherDetail> teachers = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, advisory);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int userId = rs.getInt("user_id");
            String username = rs.getString("username");
            String fullName = rs.getString("last_name") + ", " + rs.getString("first_name");
            String gender = rs.getString("gender");
            String advisoryClass = rs.getInt("grade") + "-" + rs.getString("section");
            String role = rs.getString("role");
            String email = rs.getString("email");
            String status = rs.getString("is_active");

            teachers.add(new TeacherDetail(userId, username, fullName, gender, advisoryClass, role, email, status));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return teachers;
}

}
