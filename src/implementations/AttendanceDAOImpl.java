/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import database.DBConnection;
import interfaces.AttendanceDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.Attendance;
import java.sql.*;

/**
 *
 * @author NelsonJrLHerrera
 */
public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public boolean createAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (record_id, event_id, student_id, check_in_time, check_out_time, status, synced) VALUES (?, ?, ?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getRecordId());
            stmt.setInt(2, attendance.getEventId());
            stmt.setInt(3, attendance.getStudentId());
            stmt.setString(4, now); // check-in time
            stmt.setString(5, attendance.getCheck_out_time()); // check-out time
            stmt.setString(6, attendance.getStatus());
            stmt.setBoolean(7, attendance.getSynced());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Attendance> getAllAttendance() {
        String sql = "SELECT * FROM attendance";
        List<Attendance> attendances = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attendance attendance = new Attendance(
                        rs.getInt("record_id"),
                        rs.getInt("event_id"),
                        rs.getInt("student_id"),
                        rs.getString("check_in_time"),
                        rs.getString("check_out_time"),
                        rs.getString("status"),
                        rs.getBoolean("synced")
                );
                attendances.add(attendance);
            }
            return attendances;
        }catch(SQLException e){
            return null;
        }
    }

    @Override
    public Attendance getAttendanceById(int recordId){
        Attendance attendance = null;
        String sql = "SELECT * FROM attendance WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    attendance = new Attendance(
                            rs.getInt("record_id"),
                            rs.getInt("event_id"),
                            rs.getInt("student_id"),
                            rs.getString("check_in_time"),
                            rs.getString("check_out_time"),
                            rs.getString("status"),
                            rs.getBoolean("synced")
                    );
                }
                return attendance;
        }catch(SQLException e){
            return null;
        }
    }

    @Override
    public List<Attendance> searchAttendance(String keyword){
        String sql = "SELECT * FROM attendance WHERE status LIKE ? OR synced LIKE ?";
        List<Attendance> attendances = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Attendance attendance = new Attendance(
                            rs.getInt("record_id"),
                            rs.getInt("event_id"),
                            rs.getInt("student_id"),
                            rs.getString("check_in_time"),
                            rs.getString("check_out_time"),
                            rs.getString("status"),
                            rs.getBoolean("synced")
                    );
                    attendances.add(attendance);
                }
            return attendances;
        }catch(SQLException e){
            return null;
        }
    }

    @Override
    public boolean updateAttendance(Attendance attendance){
        String sql = "UPDATE attendance SET event_id = ?, student_id = ?, check_in_time = ?, check_out_time = ?, status = ?, synced = ? WHERE record_id = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getEventId());
            stmt.setInt(2, attendance.getStudentId());
            stmt.setString(3, now); // update check-in time
            stmt.setString(4, attendance.getCheck_out_time()); // update check-out time
            stmt.setString(5, attendance.getStatus());
            stmt.setBoolean(6, attendance.getSynced());
            stmt.setInt(7, attendance.getRecordId());
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    @Override
    public boolean deleteAttendance(int recordId){
        String sql = "DELETE FROM attendance WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }
}
