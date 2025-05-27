/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Attendance;
import java.sql.SQLException;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface AttendanceDAO {
    public void createAttendance(Attendance attendance) throws SQLException;
    public List<Attendance> getAllAttendance() throws SQLException;
    public Attendance getAttendanceById(int recordId) throws SQLException;  
    public List<Attendance> searchAttendance(String keyword) throws SQLException;   
    public void updateAttendance(Attendance attendance) throws SQLException;   
    public void deleteAttendance(int recordId) throws SQLException;
}
