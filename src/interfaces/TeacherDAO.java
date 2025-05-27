/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Teacher;
import java.sql.SQLException;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface TeacherDAO {

    public void createTeacher(Teacher teacher) throws SQLException;

    public List<Teacher> getAllTeachers() throws SQLException;

    public Teacher getTeacherById(String teacherId) throws SQLException;

    public List<Teacher> searchTeachers(String keyword) throws SQLException;

    public void updateTeacher(Teacher teacher) throws SQLException;

    public void deleteTeacher(String teacherId) throws SQLException;
}
