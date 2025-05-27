/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Student;
import java.sql.SQLException;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface StudentDAO {

    public void create(Student student) throws SQLException;

    public Student read_one(int student_id) throws SQLException;

    public List<Student> read_all() throws SQLException;

    public void update(int student_id, Student student) throws SQLException;

    public List<Student> readStudentsByUser(int user_id) throws SQLException;
}
