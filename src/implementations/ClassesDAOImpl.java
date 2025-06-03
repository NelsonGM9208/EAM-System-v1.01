/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import database.DBConnection;
import interfaces.ClassesDAO;
import models.Classes;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ClassInfoDetail;

/**
 *
 * @author NelsonJrLHerrera
 */
public class ClassesDAOImpl implements ClassesDAO {

    @Override
    public boolean create(Classes classes) {
        String query = "INSERT INTO classes (grade, section, adviser_id) VALUES ( ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, classes.getGrade()); // updated
            pstmt.setString(2, classes.getSection());
            pstmt.setInt(3, classes.getAdviser_id());
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Classes read_one(int class_id) {
        Classes classes = null;
        String query = "SELECT * FROM classes WHERE class_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, class_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                classes = new Classes(
                        rs.getInt("class_id"),
                        rs.getInt("adviser_id"), // updated
                        rs.getInt("grade"),
                        rs.getString("section"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classes;
    }

    @Override
    public List<Classes> read_all() {
        List<Classes> classList = new ArrayList<>();
        String query = "SELECT * FROM classes ORDER BY class_id ASC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Classes classes = new Classes(
                        rs.getInt("class_id"),
                        rs.getInt("adviser_id"), // updated
                        rs.getInt("grade"),
                        rs.getString("section"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                classList.add(classes);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classList;
    }

    @Override
    public boolean update(Classes classes) {
        String query = "UPDATE classes SET grade = ?, section = ?, adviser_id = ? WHERE class_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, classes.getGrade()); // updated
            pstmt.setString(2, classes.getSection());
            pstmt.setInt(3, classes.getAdviser_id());
            pstmt.setInt(4, classes.getClass_id());

            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int class_id) {
        String query = "DELETE FROM classes WHERE class_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, class_id);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsWithAdviser(int grade, String section) {
        String sql = "SELECT COUNT(*) FROM classes WHERE grade = ? AND section = ? AND adviser_id IS NOT NULL";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grade);
            stmt.setString(2, section);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Classes> readAllByAdviserId(int adviserId) {
        List<Classes> classList = new ArrayList<>();
        String query = "SELECT * FROM classes WHERE adviser_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, adviserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Classes classes = new Classes(
                            rs.getInt("class_id"),
                            rs.getInt("adviser_id"),
                            rs.getInt("grade"),
                            rs.getString("section"),
                            rs.getString("created_at"),
                            rs.getString("updated_at")
                    );
                    classList.add(classes);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider using logging in production
        }

        return classList;
    }

    public Classes readOneByAdviserId(int adviserId) {
        Classes classList = null;
        String query = "SELECT * FROM classes WHERE adviser_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, adviserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    classList = new Classes(
                            rs.getInt("class_id"),
                            rs.getInt("adviser_id"),
                            rs.getInt("grade"),
                            rs.getString("section"),
                            rs.getString("created_at"),
                            rs.getString("updated_at")
                    );
                }
                return classList;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider using logging in production
        }

        return classList;
    }

    public List<Map<String, Object>> sortClassesByGradeAndSection(int grade, String section) {
        String sql = """
        SELECT c.class_id, CONCAT(u.last_name, ', ', u.first_name) AS adviser,
               CONCAT(c.grade, '-', c.section) AS grade_section,
               c.created_at, c.updated_at
        FROM classes c
        LEFT JOIN teachers t ON c.adviser_id = t.teacher_id
        LEFT JOIN users u ON t.user_id = u.user_id
        WHERE c.grade = ? AND c.section = ?
        ORDER BY c.created_at DESC
    """;

        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, grade);
            stmt.setString(2, section);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("class_id", rs.getInt("class_id"));
                row.put("adviser", rs.getString("adviser") != null ? rs.getString("adviser") : "Unassigned");
                row.put("grade_section", rs.getString("grade_section"));
                row.put("created_at", rs.getString("created_at"));
                row.put("updated_at", rs.getString("updated_at"));
                resultList.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public List<ClassInfoDetail> searchClassesByAdviserName(String searchTerm) {
        String sql = """
            SELECT c.class_id, CONCAT(u.last_name, ', ', u.first_name) AS adviser_name,
            c.grade, c.section, c.created_at, c.updated_at
            FROM classes c
            JOIN teachers t ON c.adviser_id = t.teacher_id
            JOIN users u ON t.user_id = u.user_id
            WHERE LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE ?
            ORDER BY c.grade, c.section
            """;

        List<ClassInfoDetail> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ClassInfoDetail(
                        rs.getInt("class_id"),
                        rs.getString("adviser_name"),
                        rs.getInt("grade"),
                        rs.getString("section"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Check if class has an adviser
public boolean hasAdviser(int classId) throws SQLException {
    String sql = "SELECT adviser_id FROM classes WHERE class_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, classId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getObject("adviser_id") != null;  // adviser_id not null means adviser assigned
            }
        }
    }
    return false;
}

// Check if class has students
public boolean hasStudents(int classId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM students WHERE class_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, classId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

}
