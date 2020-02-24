/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DELL
 */
public class ChatSession implements Runnable {
    private int sessionID;
    private Socket player1;
    private Socket player2;
    private DataInputStream fromPlayer1;
    private DataOutputStream toPlayer1;
    private DataInputStream fromPlayer2;
    private DataOutputStream toPlayer2;

    public ChatSession(Socket player1, int sessionID) throws IOException {
        this.player1 = player1;
        this.sessionID=sessionID;
        fromPlayer1 = new DataInputStream(player1.getInputStream());
        toPlayer1 = new DataOutputStream(player1.getOutputStream());
        
    }

    public void setPlayer2(Socket player2) throws IOException {
        this.player2 = player2;
        fromPlayer2 = new DataInputStream(player2.getInputStream());
        toPlayer2 = new DataOutputStream(player2.getOutputStream());
    }


    
    @Override
    public void run() {
        System.out.println("chat session start");
       while(player2==null){
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException ex) {
                   Logger.getLogger(HandleAsession.class.getName()).log(Level.SEVERE, null, ex);
               }
        }
        try {
            this.toPlayer1.writeUTF("Player2 connected");
            this.toPlayer2.writeUTF("chao mung toi room");
        } catch (IOException ex) {
            Logger.getLogger(ChatSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       new Thread(()->{
           try {
               while(true){
               String mess = fromPlayer1.readUTF();
               toPlayer2.writeUTF(mess);
               
               }
           } catch (IOException ex) {
               Logger.getLogger(ChatSession.class.getName()).log(Level.SEVERE, null, ex);
           }
       }).start();
       new Thread(()->{
           try {
               while(true){
               String mess = fromPlayer2.readUTF();
               
               toPlayer1.writeUTF(mess);
               }
           } catch (IOException ex) {
               Logger.getLogger(ChatSession.class.getName()).log(Level.SEVERE, null, ex);
           }
       }).start();
    }

    public int getSessionID() {
        return sessionID;
    }
    
}
