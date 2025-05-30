/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Teacher;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface TeacherDAO {

    public Integer createTeacher(Teacher teacher);

    public List<Teacher> getAllTeachers();

    public Teacher getTeacherById(String teacherId);

    public List<Teacher> searchTeachers(String keyword);

    public boolean updateTeacher(Teacher teacher);

    public boolean deleteTeacher(int teacherId);
}
