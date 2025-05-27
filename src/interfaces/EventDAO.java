/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import models.Event;
import java.util.List;
import java.sql.SQLException;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface EventDAO {

    public void createEvent(Event event) throws SQLException;

    public List<Event> readAllEvents() throws SQLException;

    public Event read_oneEvent(int EventId) throws SQLException;

    public List<Event> searchEvents(String str) throws SQLException;

    public void updateEvent(Event event) throws SQLException;

    public void deleteEvent(int EventId) throws SQLException;
}
