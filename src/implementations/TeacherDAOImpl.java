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
    public boolean createTeacher(Teacher teacher){
        String sql = "INSERT INTO teachers (specialization, advisory_class) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(3, teacher.getSpecialization());
            stmt.setString(4, teacher.getAdvisoryClass());
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    @Override
    public List<Teacher> getAllTeachers(){
        String sql = "SELECT * FROM teachers";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Teacher teacher = new Teacher(
                        rs.getInt("teacher_id"),
                        rs.getInt("user_id"),
                        rs.getString("specialization"),
                        rs.getString("advisory_class")
                );
                teachers.add(teacher);
            }
            return teachers;
        }catch(SQLException e){
            return null;
        }
    }

    @Override
    public Teacher getTeacherById(String teacherId){
        Teacher teacher = null;
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt(teacherId));
            ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    teacher = new Teacher(
                            rs.getInt("teacher_id"),
                            rs.getInt("user_id"),
                            rs.getString("specialization"),
                            rs.getString("advisory_class")
                    );
                }
                return teacher;
            }catch(SQLException e){
                return null;
            }
    }

    @Override
    public List<Teacher> searchTeachers(String str){
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
                            rs.getString("specialization"),
                            rs.getString("advisory_class")
                    );
                    teachers.add(teacher);
                }
                return teachers;
            }catch(SQLException e){
                return null;
            }
    }

    @Override
    public boolean updateTeacher(Teacher teacher){
        String sql = "UPDATE teachers SET user_id = ?, specialization = ?, advisory_class = ? WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getSpecialization());
            stmt.setString(3, teacher.getAdvisoryClass());
            stmt.setInt(4, teacher.getTeacherId());
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    @Override
    public boolean deleteTeacher(int teacherId){
        String sql = "DELETE FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }
}
