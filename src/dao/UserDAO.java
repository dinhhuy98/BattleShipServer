/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class UserDAO {
    private Connection conn;
    
    public UserDAO(){
        conn=DB.getConnection();
        
    }
    
    public User checkLogin(String user, String pass){
        try{
            String sql="SELECT * FROM users WHERE username='"+user+"'&&password='"+pass+"';";
            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery(sql);
            while(result.next()){
                return new User(result.getInt(1),result.getString(2),result.getString(3),result.getInt(4));
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }
     public ArrayList<User> highScore(){
        ArrayList<User> users = new ArrayList<User>();
        try{
            String sql="SELECT * FROM users ORDER BY score DESC;";
            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery(sql);
            while(result.next()){
                 users.add(new User(result.getInt(1),result.getString(2),result.getString(3),result.getInt(4)));
            }           
        }catch (SQLException e){
            System.out.println(e);
        }
        return users;
    }
    
     public boolean register(String username, String password){       
         try{
            String sql = "INSERT INTO Users (username, password, score) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setInt(3, 20);
            statement.execute();
        }catch (SQLException e){
            System.out.println(e);
            return false;
        }
        return true;
    }
     
    public boolean updateScore(User u){
        try{
            String sql = "UPDATE Users SET score=? WHERE id=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1,u.getScore());
            statement.setInt(2,u.getId());
            statement.executeUpdate();
        }catch (SQLException e){
            return false;
        }
        return true;
    }
}
