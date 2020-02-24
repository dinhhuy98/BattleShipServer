/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author DELL
 */
public class DB {
    
    public static Connection getConnection(){
        Connection conn=null;
        try{
            Properties props= new Properties();
            props.put("user","root");
            props.put("password","");
            props.put("useUnicode","true");
            props.put("useServerPrepStmts","false");
            props.put("characterEncoding","UTF-8");
            Class.forName("com.mysql.jdbc.Driver");
            conn=(Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/battleship",props);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e);
        }
        return conn;
    }
    
}
