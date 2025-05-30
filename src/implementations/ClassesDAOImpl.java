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
import java.util.List;

/**
 *
 * @author NelsonJrLHerrera
 */
public class ClassesDAOImpl implements ClassesDAO{
    @Override
    public boolean create(Classes classes) {
        String query = "INSERT INTO classes (grade, section, adviser_id) VALUES ( ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(2, classes.getGrade()); // updated
            pstmt.setString(3, classes.getSection());
            pstmt.setInt(4, classes.getAdviser_id());
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
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

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, class_id);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
