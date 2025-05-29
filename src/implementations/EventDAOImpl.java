/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementations;

import interfaces.EventDAO;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Event;

/**
 *
 * @author NelsonJrLHerrera
 */
public class EventDAOImpl implements EventDAO {

    @Override
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (eventName, startTime, endTime, veneu, description, geofenceId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getStartTime());
            stmt.setString(3, event.getEndTime());
            stmt.setString(4, event.getVenue());
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getGeofenceId());
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
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
                        rs.getInt("eventId"),
                        rs.getString("eventName"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getString("veneu"),
                        rs.getString("description"),
                        rs.getInt("geofenceId")
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
        String sql = "SELECT * FROM events WHERE eventId = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                event = new Event(
                        rs.getInt("eventId"),
                        rs.getString("eventName"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getString("veneu"),
                        rs.getString("description"),
                        rs.getInt("geofenceId")
                );
                // set other fields as needed
            }    
                return event;
            
        }catch(SQLException e){
            return null;
        }
    }

    @Override
    public List<Event> searchEvents(String keyword){
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE eventName LIKE ? OR description LIKE ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("eventId"),
                        rs.getString("eventName"),
                        rs.getString("startTime"),
                        rs.getString("endTime"),
                        rs.getString("veneu"),
                        rs.getString("description"),
                        rs.getInt("geofenceId")
                );
                eventList.add(event);
            }
        return eventList;
        }catch(SQLException e){
            return null;
        }
    }
    
    @Override
    public boolean updateEvent(Event event){
        String sql = "UPDATE events SET eventName = ?, startTime = ?, endTime = ?, venue = ?, description = ? WHERE geofenceId = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, event.getStartTime());
            stmt.setString(3, event.getEndTime());
            stmt.setString(4, event.getVenue());
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getGeofenceId());
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }

    
    @Override
    public boolean deleteEvent(int EventId){
        String sql = "DELETE FROM events WHERE geofenceId = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, EventId);
            stmt.executeUpdate();
            
            return true;
        }catch(SQLException e){
            return false;
        }
    }
}
