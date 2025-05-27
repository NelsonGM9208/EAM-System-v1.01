/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NelsonJrLHerrera
 */
public class Event {

    //Attributes
    private static int eventId;
    private static String eventName;
    private static String startTime;
    private static String endTime;
    private static String veneu;
    private static String description;
    private static int geofenceId;

    //Constructor
    public Event(int eventId, String eventName, String startTime, String endTime, String veneu, String description, int geofenceId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.veneu = veneu;
        this.description = description;
        this.geofenceId = geofenceId;
    }

    //Getters
    public static int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getVeneu() {
        return veneu;
    }

    public String getDescription() {
        return description;
    }

    public int getGeofenceId() {
        return geofenceId;
    }

    //Setters
    public static void setEventId(int eventId) {
        Event.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setVeneu(String veneu) {
        this.veneu = veneu;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGeofenceId(int geofenceId) {
        this.geofenceId = geofenceId;
    }
}
