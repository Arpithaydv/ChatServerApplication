/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;

import java.net.*;
import java.util.*;
import java.io.*;

public class HandleChat {
    public ArrayList<Chatrooms> chatroomslist;
    DatabaseConnect dbconn;
    HandleChat(){
    dbconn = new DatabaseConnect();
    chatroomslist = new ArrayList<Chatrooms>();
    }
    public void getChatroomList(){
    ArrayList<String> chatlist = dbconn.getListofChatrooms();
    for (int i = 0;i<chatlist.size();i++){
        Chatrooms object = new Chatrooms(chatlist.get(i));
        chatroomslist.add(object);
        object = null;
    }
    }
    public boolean addroom(String chatname){
     boolean success;
        success = dbconn.createChatRooms(chatname);
        if(success){
            Chatrooms obj = new Chatrooms(chatname);
            chatroomslist.add(obj);
            obj=null;
            return true;
        }else{
            return false;
        }
    }
    public void addClientToRoom(String chatroom_name,Socket tsock,String tusername){
        
    }
    public void leaveroom(String chatroom_name, Socket tsock){
        
    }
    
}
