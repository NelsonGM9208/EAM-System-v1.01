/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Teacher;
import models.TeacherDetail;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface TeacherDAO {

    public Integer createTeacher(Teacher teacher);

    public List<Teacher> getAllTeachers();

    public Teacher getTeacherById(int teacherId);

    public List<TeacherDetail> searchTeachers(String keyword);

    public boolean updateTeacher(Teacher teacher);

    public boolean deleteTeacher(int teacherId);
}
