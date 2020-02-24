/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDAO;
import model.Room;
import model.User;
import model.BattleShipConst;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import dao.RoomDAO;

/**
 *
 * @author DELL
 */
public class BattleShipServer implements BattleShipConst{
    public static ArrayList<Room> listRoom;
    public static ArrayList<User> listUserOnline;
    public static int sessionN=1;
    public static ArrayList<HandleAsession> listHandleAsession;
    public static ArrayList<ChatSession> listChatSession;
    public static boolean waiting=true;
    public static ChatSession chatSession;
    public static void main(String[] args) {
        UserDAO userdao = new UserDAO();
        listRoom = new ArrayList<Room>();
        listUserOnline = new ArrayList<User>();
        listHandleAsession = new ArrayList<HandleAsession>();
        listChatSession = new ArrayList<ChatSession>();
        
        try{
            ServerSocket server = new ServerSocket(2019);
            System.out.println("Watting client");
            
            while(true){
                Socket socket = server.accept();   
                new Thread(()->{
                    try{
                        Socket player = socket;
                        DataInputStream fromPlayer = new DataInputStream(player.getInputStream());
                        DataOutputStream toPlayer = new DataOutputStream(player.getOutputStream());
                        
                        boolean stop=false,stop2=false;
                        User u=null;
                        while(!stop){
                            int action = fromPlayer.readInt();
                            if(action==LOGIN){
                                String user = fromPlayer.readUTF();
                                String pass= fromPlayer.readUTF();
                                System.out.println(user+" "+pass);
                                u = userdao.checkLogin(user, pass);
                                if(u!=null){  //check user, pass
                                    System.out.println(u + "logged");
                                    toPlayer.writeInt(LOGIN_SUCCESS);
                                    
                                    listUserOnline.add(u);
                                    stop=true;
                                }
                                else{
                                    toPlayer.writeInt(LOGIN_ERROR);
                                }
                            }
                            else if(action==REGISTER){
                                 int action2 = fromPlayer.readInt();
                                 if(action2==REGISTER){
                                     String username = fromPlayer.readUTF();
                                     String password = fromPlayer.readUTF();
                                     if(new UserDAO().register(username,password)){
                                         toPlayer.writeInt(REGISTER_SUCCESS);
                                     }
                                     else
                                         toPlayer.writeInt(REGISTER_ERROR);
                                 }
                            }
                            else if(action==CHAT){
                                
                                int action2=fromPlayer.readInt();
                                
                                
                                
                                if(action2==NEW_CHAT){
                                    
                                    int idChat=fromPlayer.readInt();
                                    
                                    chatSession = new ChatSession(player,idChat);
                                    listChatSession.add(chatSession);
                                    new Thread(chatSession).start();
                                }
                                else if(action2==JOIN_CHAT){
                                    
                                    int id = fromPlayer.readInt();
                                     
                                    for(ChatSession c:listChatSession){
                                        if(c.getSessionID()==id){
                                            c.setPlayer2(player);
                                        }
                                    }
                                }
                                waitForPlayerAction();
                                
                            }
                        }
                        ObjectInputStream fromPlayerOject=null;
                        ObjectOutputStream toPlayerObject=null;
                       if(!stop2){
                        fromPlayerOject = new ObjectInputStream(player.getInputStream());
                        toPlayerObject = new ObjectOutputStream(player.getOutputStream());
                        toPlayerObject.writeObject(u);
                           System.out.println("gui room");
                       }
                        while(!stop2){
                            int action = fromPlayer.readInt();
                            System.out.println("doc click"+action);
                            if(action==CREATE_ROOM){
                                int action2=fromPlayer.readInt();
                                if(action2==CREATE_ROOM){
                                    
                                    String roomName = fromPlayer.readUTF();
                                    Room room = new Room();
                                    room.setPlayer1(u.getUsername());
                                    
                                    room.setId(new RoomDAO().getMaxID()+1);
                                    room.setNameRoom(roomName);
                                    listRoom.add(room);
                                    new RoomDAO().insert(roomName, u.getId());
                                    HandleAsession handleAsession = new HandleAsession(sessionN);
                                    sessionN++;
                                    handleAsession.setPlayer1(player);
                                    handleAsession.setRoom(room);
                                    toPlayer.writeInt(room.getId());
                                    listHandleAsession.add(handleAsession);
                            
                                   // chatSession.setUser1(u.getUsername());
                                    new Thread(handleAsession).start();
                                    waitForPlayerAction();
                                }
                            }
                            else if(action==LIST_ROOM){
                                toPlayerObject.writeObject(listRoom);
                                int action2 = fromPlayer.readInt();
                                if(action2==INTO_ROOM){
                                    try {
                                        Room b = (Room)fromPlayerOject.readObject();
                                        
                                        b.setPlayer2(u.getUsername());
                                        for(Room c:listRoom){
                                            if(c.getId()==b.getId())
                                                c.setPlayer2(b.getPlayer2());
                                        }
                                        for(HandleAsession e:listHandleAsession){
                                            if(e.getRoom().getId()==b.getId()){
                                                e.setRoom(b);
                                                e.setPlayer2(player);
                                            }       
                                        }
                                        System.out.println("doc room"+b);
                                        waitForPlayerAction();
                                       // new Thread(new B(player)).start();
                                        stop=true;
                                    } catch (ClassNotFoundException ex) {
                                        Logger.getLogger(BattleShipServer.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                            else if(action==HIGHSCORE){
                                toPlayerObject.writeObject(new UserDAO().highScore());
                            }
                            else if(action==GUIDE){
                                fromPlayer.readInt();
                            }
                            
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    } catch (SQLException ex) {
                        Logger.getLogger(BattleShipServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
                
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private static void waitForPlayerAction(){
        while(waiting){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(BattleShipServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        waiting=true;
    }
}
