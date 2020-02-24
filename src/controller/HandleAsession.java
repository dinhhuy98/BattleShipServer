/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Room;
import model.User;
import model.BattleShipConst;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import dao.UserDAO;

/**
 *
 * @author DELL
 */
public class HandleAsession implements Runnable, BattleShipConst{
    private int sessionN;
    private Socket player1;
    private Socket player2=null;
    private User user1;
    private User user2;
    private Room room;
    private ArrayList<Integer> locationShipP1;
    private ArrayList<Integer> locationShipP2;
    private int scoreP1=0;
    private int scoreP2=0;
    private DataInputStream fromPlayer1;
    private DataOutputStream toPlayer1;
    private DataInputStream fromPlayer2;
    private DataOutputStream toPlayer2;
    private ObjectOutputStream toPlayer1Object;
    private ObjectOutputStream toPlayer2Object;
    private ObjectInputStream fromPlayer1Object;
    private ObjectInputStream fromPlayer2Object;
    private boolean continueToPlay = true;
    @Override
    
    public void run() {
        try {
            
           fromPlayer1 = new DataInputStream(player1.getInputStream());
           toPlayer1 = new DataOutputStream(player1.getOutputStream());
           toPlayer1Object = new ObjectOutputStream(player1.getOutputStream());
           fromPlayer1Object = new ObjectInputStream(player1.getInputStream());
           toPlayer1Object.writeObject(room);
           user1 = (User)fromPlayer1Object.readObject();
            //System.out.println("yyy");
           while(player2==null){
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException ex) {
                   Logger.getLogger(HandleAsession.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
            System.out.println("klklkllk");
            fromPlayer2 = new DataInputStream(player2.getInputStream());
            toPlayer2 = new DataOutputStream(player2.getOutputStream());
            toPlayer2Object=new ObjectOutputStream(player2.getOutputStream());
            fromPlayer2Object = new ObjectInputStream(player2.getInputStream());
            toPlayer2Object.writeObject(room);
            user2 = (User)fromPlayer2Object.readObject();
            
            System.out.println("player2 connected");
            toPlayer1.writeInt(1);
            toPlayer1.writeUTF("Người chơi "+room.getPlayer2()+" đã tham gia phòng");
            toPlayer1Object.writeObject(room);
            toPlayer2.writeUTF("Chào mừng tới phòng");
            toPlayer1Object.writeObject(user2);
            toPlayer2Object.writeObject(user1);
            //////
            boolean stop=false;
            while(!stop){
                System.out.println("first");
                toPlayer1.writeUTF("Nhấn sẵn sàng để bắt đầu chơi");
                toPlayer2.writeUTF("Nhấn sẵn sàng để bắt đầu chơi");
                System.out.println("second");
                int action1 = fromPlayer1.readInt();
                int action2 = fromPlayer2.readInt();
                System.out.println("third");
                if(action1==READY && action2==READY){
                    toPlayer1.writeInt(READY);
                    toPlayer2.writeInt(READY);
                }
                System.out.println("start");
                scoreP1=0;
                scoreP2=0;
                play();
                System.out.println("end play");
                updateToDB();
            }
                              
        } catch (IOException ex) {
            Logger.getLogger(HandleAsession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HandleAsession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HandleAsession(int sessionN) {
        
        this.sessionN=sessionN;
       
    }

    public HandleAsession() {
    }

    public void setPlayer2(Socket player2) {
        this.player2 = player2;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setPlayer1(Socket player1) {
        this.player1 = player1;
    }

    public Socket getPlayer2() {
        return player2;
    }

    public int getSessionN() {
        return sessionN;
    }

    public Room getRoom() {
        return room;
    }
    
    public boolean checkCorrect(int player,int k){
        if(player==PLAYER1){
            if(locationShipP1.contains(k))
                return true;
            else return false;
        }
        else if(player==PLAYER2){
            if(locationShipP2.contains(k))
                return true;
            else return false;
        }
        return false;
    }
    public void sendMove(DataOutputStream toPlayer,int k1, int k2){
        try {
            toPlayer.writeInt(k1);
            toPlayer.writeInt(k2);
        } catch (IOException ex) {
            Logger.getLogger(HandleAsession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void play() throws IOException, ClassNotFoundException{
         locationShipP1 = (ArrayList<Integer>)fromPlayer1Object.readObject();
         locationShipP2 = (ArrayList<Integer>)fromPlayer2Object.readObject();
            for(int i=0; i<5;i++){
                System.out.println(locationShipP1.get(i)+" "+locationShipP2.get(i));
            }
            boolean stop=false;
        while(!stop){
                //nhan tu guessShip-P1 & gui toi checkCorrect-P1
                int k1 = fromPlayer1.readInt();
                int k2 = fromPlayer1.readInt();
                System.out.println("nhan k1,k2 p1: "+k1+" "+k2);
                if(checkCorrect(PLAYER2,k1)){
                    scoreP1++;
                    toPlayer1.writeInt(CORRECT);
                    System.out.println("gui corect toi p1");
                }
                else{
                    toPlayer1.writeInt(INCORRECT);
                    System.out.println("gui incorect toi p1");
                }
                
                if(checkCorrect(PLAYER2,k2)){
                    scoreP1++;
                    toPlayer1.writeInt(CORRECT);
                    System.out.println("gui corect toi p1");
                }
                else{
                    toPlayer1.writeInt(INCORRECT);
                    System.out.println("gui incorect toi p1");
                }
                sendMove(toPlayer2,k1,k2); //gui toi receverMove-P2
                System.out.println("gui toi p2 lua chon cua p1");
                //check win va gui status toi P1,P2
                if(scoreP1>=5){
                    toPlayer1.writeInt(PLAYER1_WON);
                    toPlayer2.writeInt(PLAYER1_WON);
                    System.out.println("gui p1 thang toi p1,p2");
                    stop=true;
                    break;
                }
                else{
                    toPlayer1.writeInt(CONTINUE);
                    toPlayer2.writeInt(CONTINUE);
                    System.out.println("gui tiep tuc choi");
                }
                
                //nhan guessShip-P2 va gui checkCorrect-P2
                k1 = fromPlayer2.readInt();
                k2 = fromPlayer2.readInt();
                System.out.println("nhan k1,k2 tu p2:"+k1+" "+k2);
                if(checkCorrect(PLAYER1,k1)){
                    scoreP2++;
                    toPlayer2.writeInt(CORRECT);
                    System.out.println("gui correct toi p2");
                }
                else{
                    toPlayer2.writeInt(INCORRECT);
                    System.out.println("gui incorrect toi p2");
                }
                
                if(checkCorrect(PLAYER1,k2)){
                    scoreP2++;
                    toPlayer2.writeInt(CORRECT);
                    System.out.println("gui correct toi p2");
                }
                else{
                    toPlayer2.writeInt(INCORRECT);
                    System.out.println("gui correct toi p2");
                }
                sendMove(toPlayer1,k1,k2);
                System.out.println("gui top p1 lua chon p2");
                if(scoreP2>=5){
                    toPlayer1.writeInt(PLAYER2_WON);
                    toPlayer2.writeInt(PLAYER2_WON);
                    System.out.println("gui p2 thang toi p1,p2");
                    
                    System.out.println("gui toi p1 lua chon cua p2");
                    stop=true;
                    break;
                }
                else{
                    toPlayer1.writeInt(CONTINUE);
                    toPlayer2.writeInt(CONTINUE);
                    System.out.println("gui tep tuc choi");
                    System.out.println("gui toi p1 lua chon cua p2");
                }
              System.out.println("-----------------------------");
            }
            

    }
    public void updateToDB() throws IOException{
        user1.setScore(fromPlayer1.readInt());
                user2.setScore(fromPlayer2.readInt());
                System.out.println("nhan score:"+ user1.getScore()+" "+user2.getScore());
                new UserDAO().updateScore(user1);
                new UserDAO().updateScore(user2);
    }
    
}
