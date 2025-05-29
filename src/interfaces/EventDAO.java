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

    public boolean createEvent(Event event);

    public List<Event> readAllEvents();

    public Event read_oneEvent(int EventId);

    public List<Event> searchEvents(String str);

    public boolean updateEvent(Event event);

    public boolean deleteEvent(int EventId);
}
