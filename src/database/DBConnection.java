/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author NelsonJrLHerrera
 */
public class DBConnection {
      // Database URL, Username, and Password
    private static final String URL = "jdbc:mysql://localhost/eam-system";      
    private static final String USER = "root";      
    private static final String PASSWORD = "root";  

    //Database Connection
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /*public static void main(String args[]){
        Connection conn = DBConnection.getConnection();
        
        if(conn == null){
            System.out.println("Error was encountered!");
        } else {
            System.out.println("Connection was successful!");
        }
    
    }*/
}