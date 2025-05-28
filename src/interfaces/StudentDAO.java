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

    public boolean create(Student student);

    public Student read_one(int student_id);

    public List<Student> read_all();

    public boolean update(Student student);
    
    public boolean delete(int student_id);
}
