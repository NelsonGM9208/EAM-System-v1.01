/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Classes;

/**
 *
 * @author NelsonJrLHerrera
 */
public interface ClassesDAO {
    
    public boolean create(Classes classes);
    
    public Classes read_one(int class_id);
    
    public List<Classes> read_all();
    
    public boolean update(Classes classes);
    
    public boolean delete(int class_id);
}