/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server_Side {
    
    public static HandleChat handle_chat;
    
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            final int serverPort = 8000;
            ServerSocket server_socket = new ServerSocket(serverPort);
            handle_chat = new HandleChat();
            handle_chat.getChatroomList();
            for(int i=0 ; i<handle_chat.chatroomslist.size() ; i++){
                //System.out.println("yo");
               Chatrooms obj = (Chatrooms)handle_chat.chatroomslist.get(i);
               System.err.println(obj.name_of_chatroom);
            }
            while(true){
                Socket client_socket = server_socket.accept();
                
                System.out.println("Connection to the server successfully accepted");
                
                Server_Side_object newconnection = new Server_Side_object(client_socket, handle_chat);
                Thread threadobject = new Thread(newconnection);
                threadobject.start();
            }
            
            
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
