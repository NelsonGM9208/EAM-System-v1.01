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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.AttendanceDetail;

/**
 *
 * @author NelsonJrLHerrera
 */
public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public boolean createAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendances (record_id, event_id, student_id, check_in_time, check_out_time, remark) VALUES (?, ?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getRecordId());
            stmt.setInt(2, attendance.getEventId());
            stmt.setInt(3, attendance.getStudentId());
            stmt.setString(4, now); // check-in time
            stmt.setString(5, attendance.getCheck_out_time()); // check-out time
            stmt.setString(6, attendance.getRemark());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Attendance> getAllAttendance() {
        String sql = "SELECT * FROM attendances";
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
                        rs.getString("remark")
                );
                attendances.add(attendance);
            }
            return attendances;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Attendance getAttendanceById(int recordId) {
        Attendance attendance = null;
        String sql = "SELECT * FROM attendances WHERE record_id = ?";
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
                        rs.getString("remark")
                );
            }
            return attendance;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Attendance> searchAttendance(String keyword) {
        String sql = "SELECT * FROM attendances WHERE remark LIKE ? OR synced LIKE ?";
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
                        rs.getString("remark")
                );
                attendances.add(attendance);
            }
            return attendances;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean updateAttendance(Attendance attendance) {
        String sql = "UPDATE attendances SET event_id = ?, student_id = ?, check_in_time = ?, check_out_time = ?, remark = ? WHERE record_id = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getEventId());
            stmt.setInt(2, attendance.getStudentId());
            stmt.setString(3, now); // update check-in time
            stmt.setString(4, attendance.getCheck_out_time()); // update check-out time
            stmt.setString(5, attendance.getRemark());
            stmt.setInt(6, attendance.getRecordId());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteAttendance(int recordId) {
        String sql = "DELETE FROM attendances WHERE record_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Attendance> getAttendanceByClassId(int classId) {
        List<Attendance> attendanceList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.* FROM attendances a "
                    + "JOIN students s ON a.student_id = s.student_id "
                    + "WHERE s.class_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance(
                        rs.getInt("record_id"),
                        rs.getInt("event_id"),
                        rs.getInt("student_id"),
                        rs.getString("check_in_time"),
                        rs.getString("check_out_time"),
                        rs.getString("remark")
                );
                attendanceList.add(attendance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attendanceList;
    }

    public void refreshAttendanceTBL(JTable table) {
        String sql = "SELECT CONCAT(u.last_name, ', ', u.first_name) AS student_name, "
                + "e.event_name, e.date, a.check_in_time, a.check_out_time, a.remark "
                + "FROM attendances a "
                + "JOIN students s ON a.student_id = s.student_id "
                + "JOIN users u ON s.user_id = u.user_id "
                + "JOIN events e ON a.event_id = e.event_id "
                + "ORDER BY e.date DESC, student_name ASC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                String studentName = rs.getString("student_name");
                String eventName = rs.getString("event_name");
                java.sql.Date eventDate = rs.getDate("date");
                java.sql.Timestamp checkInTime = rs.getTimestamp("check_in_time");
                java.sql.Timestamp checkOutTime = rs.getTimestamp("check_out_time");
                String remark = rs.getString("remark");

                model.addRow(new Object[]{
                    studentName,
                    eventName,
                    eventDate,
                    checkInTime,
                    checkOutTime,
                    remark
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to refresh attendance table.");
        }
    }

    public List<Attendance> getAttendanceByStudentId(int student_id) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendances WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, student_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance(
                        rs.getInt("record_id"),
                        rs.getInt("event_id"),
                        rs.getInt("student_id"),
                        rs.getString("check_in_time"),
                        rs.getString("check_out_time"),
                        rs.getString("remark")
                );
                attendanceList.add(attendance);
            }

            return attendanceList;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AttendanceDetail> filterAttendance(String eventFilter, String gradeSectionFilter, String remarkFilter) {
    List<AttendanceDetail> results = new ArrayList<>();

    String sql = """
        SELECT
            a.record_id,
            a.event_id,
            e.event_name,
            a.student_id,
            CONCAT(us.first_name, ' ', us.last_name) AS student_name,
            CONCAT(s.grade_level, '-', s.section) AS grade_section,
            CONCAT(ut.first_name, ' ', ut.last_name) AS adviser_name,
            e.date,
            a.check_in_time,
            a.check_out_time,
            a.remark
        FROM attendances a
        JOIN events e ON a.event_id = e.event_id
        JOIN students s ON a.student_id = s.student_id
        JOIN users us ON s.user_id = us.user_id
        JOIN classes c ON s.class_id = c.class_id
        JOIN teachers t ON c.adviser_id = t.teacher_id
        JOIN users ut ON t.user_id = ut.user_id
        WHERE
            (? = 'All' OR e.event_name = ?)
            AND (? = 'All' OR CONCAT(s.grade_level, '-', s.section) = ?)
            AND (? = 'All' OR a.remark = ?)
        ORDER BY e.date DESC, a.check_in_time ASC
        """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        // Set query parameters for filters (each used twice)
        ps.setString(1, eventFilter);
        ps.setString(2, eventFilter);
        ps.setString(3, gradeSectionFilter);
        ps.setString(4, gradeSectionFilter);
        ps.setString(5, remarkFilter);
        ps.setString(6, remarkFilter);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AttendanceDetail detail = new AttendanceDetail(
                    rs.getInt("record_id"),
                    rs.getInt("event_id"),
                    rs.getString("event_name"),
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("grade_section"),
                    rs.getString("adviser_name"),
                    rs.getDate("date"),
                    rs.getString("check_in_time"),
                    rs.getString("check_out_time"),
                    rs.getString("remark")
                );
                results.add(detail);
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error retrieving attendance data:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    return results;
}

}
