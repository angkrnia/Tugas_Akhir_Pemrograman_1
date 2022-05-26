/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laundry;
import java.sql.*;
/**
 *
 * @author angga
 */
public class Database {
    
    public Connection connect(){
        
        String DB = "jdbc:mysql://localhost/laundry";
        String USER = "root";
        String PW = "";
        
        Connection conn = null;
        
        try{
            // driver
            Class.forName("com.mysql.jdbc.Driver");
            
            // test connection to mysql database
            conn = DriverManager.getConnection(DB,USER,PW);
            
        } catch (ClassNotFoundException | SQLException e){
            System.out.println("Gagal terkoneksi!\n->"+e);
        }
        
        return conn;   
    }
}
