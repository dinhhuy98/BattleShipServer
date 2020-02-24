/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class Room implements Serializable {
    private int id;
    private String player1;
    private String player2;
    private String winner;
    private String nameRoom;
    

    @Override
    public String toString() {
        String temp=(player2.equals(""))? "?":player2;
        return "Room"+ id + ":" + nameRoom+" <"+player1+","+temp+">" ;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public Room() {
        this.player1="";
        this.player2="";
    }
    public boolean isFull(){
        return !(this.player1.equals("")&&this.player2.equals(""));
    }

    public int getId() {
        return id;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getNameRoom() {
        return nameRoom;
    }
    
}
