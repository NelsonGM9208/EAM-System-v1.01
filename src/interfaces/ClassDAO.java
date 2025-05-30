/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import models.Class;

/**
 *
 * @author HansBVitorillo
 */
public interface ClassDAO {
   

    public boolean create(Class class);

    public Class read_one(int class_id);

    public List<Class> read_all();

    public boolean update(Class class);
    
    public boolean delete(int class_id);
}


