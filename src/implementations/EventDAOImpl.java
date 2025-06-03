/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import interfaces.EventDAO;
import database.DBConnection;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import models.Event;

/**
 *
 * @author NelsonJrLHerrera
 */
public class EventDAOImpl implements EventDAO {

    @Override
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (event_name, date, start_time, end_time, venue, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getDate());
            stmt.setString(3, event.getStartTime());
            stmt.setString(4, event.getEndTime());
            stmt.setString(5, event.getVenue());
            stmt.setString(6, event.getDescription());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Event> readAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("venue"),
                        rs.getString("description"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("status")
                );
                eventList.add(event);
            }
            return eventList;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Event read_oneEvent(int eventId) {
        Event event = null;
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("venue"),
                        rs.getString("description"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("status")
                );
                // set other fields as needed
            }
            return event;

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Event> searchEvents(String keyword) {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE event_name LIKE ? OR description LIKE ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("venue"),
                        rs.getString("description"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("status")
                );
                eventList.add(event);
            }
            return eventList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET eventName = ?, startTime = ?, endTime = ?, venue = ?, description = ? WHERE geofenceId = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getStartTime());
            stmt.setString(3, event.getEndTime());
            stmt.setString(4, event.getVenue());
            stmt.setString(5, event.getDescription());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean deleteEvent(int EventId) {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, EventId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String determineStatus(String eventDate, String endTime) {
    try {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDate date = LocalDate.parse(eventDate, dateFormatter);
        LocalTime end = LocalTime.parse(endTime, timeFormatter);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime eventEnd = date.atTime(end);

        if (now.isBefore(startOfDay)) {
            return "Upcoming";
        } else if (now.isAfter(eventEnd)) {
            return "Finished";
        } else {
            return "Ongoing";
        }

    } catch (Exception e) {
        return "Unknown";
    }
}
    
    public List<Event> getEventsByStatus(String status) {
    String sql = "SELECT * FROM events WHERE status = ? ORDER BY date ASC, start_time ASC";
    List<Event> events = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, status);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Event event = new Event(
                rs.getInt("event_id"),
                rs.getString("event_name"),
                rs.getString("date"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getString("venue"),
                rs.getString("description"),
                rs.getString("created_at"),
                rs.getString("updated_at"),
                rs.getString("status")
            );
            events.add(event);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return events;
}
    
    public void finalizeAttendanceForEvent(int eventId) {
    String sql = """
        INSERT INTO attendances (event_id, student_id, check_in_time, check_out_time, remark)
        SELECT ?, s.student_id, NULL, NULL, 'Absent'
        FROM students s
        WHERE s.student_id NOT IN (
            SELECT a.student_id
            FROM attendances a
            WHERE a.event_id = ?
        )
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, eventId);
        ps.setInt(2, eventId);

        int affectedRows = ps.executeUpdate();
        JOptionPane.showMessageDialog(null, 
            "Attendance finalized. " + affectedRows + " students marked as Absent.", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Error finalizing attendance:\n" + e.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

}
