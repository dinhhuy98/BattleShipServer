/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Room;

/**
 *
 * @author DELL
 */
public class RoomDAO {
    private Connection conn;

    public RoomDAO() {
        this.conn = DB.getConnection();
    }
    public void insert(String name, int id_chuphong){
        try{
            String sql = "INSERT INTO room (name, id_chuphong) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2,id_chuphong);
            statement.execute();
        }catch (SQLException e){
            System.out.println(e);
            
        }
        
    }
    public int getMaxID() throws SQLException{
        String sql="select max(id) from room";
        Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery(sql);
        result.next();
        return result.getInt(1);
    }
    
}
