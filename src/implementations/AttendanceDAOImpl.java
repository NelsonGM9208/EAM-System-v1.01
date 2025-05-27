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

    public void createAttendance(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (record_id, event_id, student_id, check_in_time, check_out_time, status, synced) VALUES (?, ?, ?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getRecordId());
            stmt.setInt(2, attendance.getEventId());
            stmt.setInt(3, attendance.getStudentId());
            stmt.setString(4, now); // check-in time
            stmt.setString(5, now); // check-out time
            stmt.setString(6, attendance.getStatus());
            stmt.setString(7, attendance.getSynced());
            stmt.executeUpdate();
        }
    }

    public List<Attendance> getAllAttendance() throws SQLException {
        String sql = "SELECT * FROM attendance";
        List<Attendance> attendances = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Attendance attendance = new Attendance(
                        rs.getInt("record_id"),
                        rs.getInt("event_id"),
                        rs.getInt("student_id"),
                        rs.getString("check_in_time"),
                        rs.getString("check_out_time"),
                        rs.getString("status"),
                        rs.getString("synced")
                );
                attendances.add(attendance);
            }
        }
        return attendances;
    }

    public Attendance getAttendanceById(int recordId) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Attendance(
                            rs.getInt("record_id"),
                            rs.getInt("event_id"),
                            rs.getInt("student_id"),
                            rs.getString("check_in_time"),
                            rs.getString("check_out_time"),
                            rs.getString("status"),
                            rs.getString("synced")
                    );
                }
            }
        }
        return null;
    }

    public List<Attendance> searchAttendance(String keyword) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE status LIKE ? OR synced LIKE ?";
        List<Attendance> attendances = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance attendance = new Attendance(
                            rs.getInt("record_id"),
                            rs.getInt("event_id"),
                            rs.getInt("student_id"),
                            rs.getString("check_in_time"),
                            rs.getString("check_out_time"),
                            rs.getString("status"),
                            rs.getString("synced")
                    );
                    attendances.add(attendance);
                }
            }
        }
        return attendances;
    }

    public void updateAttendance(Attendance attendance) throws SQLException {
        String sql = "UPDATE attendance SET event_id = ?, student_id = ?, check_in_time = ?, check_out_time = ?, status = ?, synced = ? WHERE record_id = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getEventId());
            stmt.setInt(2, attendance.getStudentId());
            stmt.setString(3, now); // update check-in time
            stmt.setString(4, now); // update check-out time
            stmt.setString(5, attendance.getStatus());
            stmt.setString(6, attendance.getSynced());
            stmt.setInt(7, attendance.getRecordId());
            stmt.executeUpdate();
        }
    }

    public void deleteAttendance(int recordId) throws SQLException {
        String sql = "DELETE FROM attendance WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.executeUpdate();
        }
    }
}
