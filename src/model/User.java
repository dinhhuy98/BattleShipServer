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
public class User implements Serializable{
    public static final long UID=1L;
    private int id;
    private String username;
    private String password;
    private int score;

    public User(int id, String username, String password, int score) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", score=" + score + '}';
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }
    
}
