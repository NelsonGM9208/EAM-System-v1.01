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
import models.StudentDetail;

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
    
    public List<StudentDetail> searchStudents(String keyword) {
    String sql = """
        SELECT s.user_id, s.lrn, u.first_name, u.last_name, u.gender, c.grade, c.section, u.role, u.email, u.is_active
        FROM students s
        JOIN users u ON s.user_id = u.user_id
        JOIN classes c ON s.class_id = c.class_id
        WHERE 
            CAST(s.user_id AS CHAR) LIKE ? OR
            CAST(s.lrn AS CHAR) LIKE ? OR
            CONCAT(u.first_name, ' ', u.last_name) LIKE ? OR
            u.gender LIKE ? OR
            CAST(c.grade AS CHAR) LIKE ? OR
            c.section LIKE ? OR
            u.role LIKE ? OR
            u.email LIKE ? OR
            u.is_active LIKE ?
    """;

    List<StudentDetail> results = new ArrayList<>();
    String pattern = "%" + keyword + "%";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        for (int i = 1; i <= 9; i++) {
            stmt.setString(i, pattern);
        }

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int userId = rs.getInt("user_id");
            long lrn = rs.getLong("lrn");
            String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
            String gender = rs.getString("gender");
            int grade = rs.getInt("grade"); // assuming grade is integer in DB
            String section = rs.getString("section");
            String role = rs.getString("role");
            String email = rs.getString("email");
            String is_active = rs.getString("is_active");

            StudentDetail detail = new StudentDetail(userId, lrn, fullName, gender, grade, section, role, email, is_active);
            results.add(detail);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return results;
}
    
    public List<StudentDetail> sortStudentsByGradeAndSection(int grade, String section) {
    String sql = """
        SELECT s.user_id, s.lrn, u.first_name, u.last_name, u.gender,
               c.grade, c.section, u.role, u.email, u.is_active
        FROM students s
        JOIN users u ON s.user_id = u.user_id
        JOIN classes c ON s.class_id = c.class_id
        WHERE c.grade = ? AND c.section = ?
        ORDER BY u.last_name ASC, u.first_name ASC
    """;

    List<StudentDetail> students = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, grade);
        stmt.setString(2, section);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int userId = rs.getInt("user_id");
            long lrn = rs.getLong("lrn");
            String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
            String gender = rs.getString("gender");
            int gradeLevel = rs.getInt("grade");
            String sec = rs.getString("section");
            String role = rs.getString("role");
            String email = rs.getString("email");
            String is_active = rs.getString("is_active");

            StudentDetail student = new StudentDetail(userId, lrn, fullName, gender, gradeLevel, sec, role, email, is_active);
            students.add(student);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return students;
}
}
