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
    
    public boolean createAttendance(Attendance attendance);
    
    public List<Attendance> getAllAttendance();
    
    public Attendance getAttendanceById(int recordId);  
    
    public List<Attendance> searchAttendance(String keyword); 
    
    public boolean updateAttendance(Attendance attendance);  
    
    public boolean deleteAttendance(int recordId);
}
