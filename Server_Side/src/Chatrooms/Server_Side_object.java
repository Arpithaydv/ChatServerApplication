/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server_Side_object implements Runnable{
    public static ArrayList<Socket> clientsockets = new ArrayList<Socket>();
    public static ArrayList<Socket> clientInfo = new ArrayList<Socket>();
    public static ArrayList<ObjectInputStream> inputstream = new ArrayList<ObjectInputStream>();
    public static ArrayList<ObjectOutputStream> outputstream = new ArrayList<ObjectOutputStream>();
    public Socket socket1;
    public ObjectInputStream objinputstrm;
    public ObjectOutputStream objoutputstrm;
    public Message msg;
    public DatabaseConnect dbconn;
    public String username;
    public HandleChat handle_chat;

    
    public Server_Side_object(Socket csock,HandleChat chat_room){
    this.socket1  = csock;
    handle_chat = chat_room;
    try {
        objinputstrm = new ObjectInputStream(socket1.getInputStream());
        objoutputstrm = new ObjectOutputStream(socket1.getOutputStream());
        objoutputstrm.flush();
        
        clientsockets.add(socket1);
        inputstream.add(objinputstrm);
        outputstream.add(objoutputstrm);
        
        dbconn = new DatabaseConnect();
     }catch(IOException ex){
     Logger.getLogger(Server_Side_object.class.getName()).log(Level.SEVERE, null, ex);
     }
    }
    
    @Override
    public void run(){
        try{
            try{
                while(true){
                    if(objinputstrm.available()<0){
                        return;
                    }
                    msg = (Message)objinputstrm.readObject();
                    
                    System.out.println(msg);
                    if(msg.type.equals("connection")){
                    
                        Message msg1 = new Message("connection", "server","true","test");
                        objoutputstrm.writeObject(msg1);
                        objoutputstrm.flush();
                        }else if(msg.type.equals("SignUP")){
                    
                            String user_name = msg.sender;
                            String password = msg.content;
                            boolean check1 = dbconn.SignUP(user_name, password);
                            if(check1){
                                System.out.println("Successful Signup done");
                                username = user_name;
                                Message msg1 = new Message("SignUP", "server", "true", user_name);
                                objoutputstrm.writeObject(msg1);
                                objoutputstrm.flush();
                            }else{
                                Message msg1 = new Message("SignUP","server","false",user_name);
                                objoutputstrm.writeObject(msg1);
                                objoutputstrm.flush();
                            }
                        }else if(msg.type.equals("SignIn")){
                            String user_name = msg.sender;
                            String password = msg.content;
                            boolean check1 = dbconn.SignIn(user_name, password);
                            if(check1){
                                System.out.println("Successful login");
                                username = user_name;
                                Message msg1 = new Message("SignIn", "server", "true", user_name);
                                objoutputstrm.writeObject(msg1);
                                objoutputstrm.flush();
                            }else{
                                Message msg1 = new Message("SignIn","server","false",user_name);
                                objoutputstrm.writeObject(msg1);
                                objoutputstrm.flush();
                            }
                        }else if(msg.type.equals("Create")){
                            String name_of_Chatroom = msg.content;
                            boolean success = handle_chat.addroom(name_of_Chatroom);
                            Message msg1 = null;
                            if(success){
                            msg1 = new Message("Create", msg.sender, "true", name_of_Chatroom);
                            }else{
                                msg1 = new Message("Create", msg.sender, "false", name_of_Chatroom);
                            }
                            for(int i = 0;i<clientsockets.size();i++){
                               
                               Socket sock1 = (Socket)clientsockets.get(i);
                               ObjectOutputStream tobj = outputstream.get(i);
                               tobj.writeObject(msg1);
                               tobj.flush();
                            }  
                        }else if(msg.type.equals("message")){
                            String sender = msg.sender;
                            String TextMsg = msg.content;
                            String client = msg.recipient;
                            Chatrooms askchatroom = null;
                            for(int i=0;i<handle_chat.chatroomslist.size();i++){
                                Chatrooms obj = handle_chat.chatroomslist.get(i);
                                if(obj.name_of_chatroom.equals(client)){
                                    askchatroom=(Chatrooms)obj;
                                    break;
                                }
                            }
                            Message msg1 = new Message("message", sender, TextMsg, client);
                            for(int i = 0;i<askchatroom.chatarraysockets.size();i++){
                                Socket sock4 = (Socket)askchatroom.chatarraysockets.get(i);
                                int inde_of_socket = clientsockets.indexOf(sock4);
                                ObjectOutputStream tobj = outputstream.get(inde_of_socket);
                                tobj.writeObject(msg1);
                                tobj.flush();
                            }
                        }else if (msg.type.equals("leave")){
                            String whaich_chatroom = msg.content;
                            String who_leaves = msg.sender;
                            
                            for(int i=0;i<handle_chat.chatroomslist.size();i++){
                           Chatrooms obj = (Chatrooms)handle_chat.chatroomslist.get(i);
                           if(obj.name_of_chatroom.equals(whaich_chatroom)){
                               System.out.println("found");
                               int index = obj.chatarraysockets.indexOf(socket1);
                               for(int j=0;j<clientsockets.size();j++){
                                   Socket csock = (Socket)clientsockets.get(j);
                                   ObjectOutputStream objOp = (ObjectOutputStream)outputstream.get(j);
                                   objOp.writeObject(new Message("leave", "server", who_leaves, whaich_chatroom));
                                }
                                obj.chatarraysockets.remove(index);
                                obj.users.remove(index);
                                break;
                            }
                        }
                    }else if(msg.type.equals("logout")){
                        objoutputstrm.writeObject(new Message("logout", "server", "true", "user"));
                        objoutputstrm.flush();
                        int index = clientsockets.indexOf(socket1);
                       clientsockets.remove(index);
                       clientInfo.remove(index);
                    }else if(msg.type.equals("getChatroomList")){
                        ArrayList<String> chatrooms_List = new ArrayList<String>();
                        for(int i=0;i<handle_chat.chatroomslist.size();i++){
                           Chatrooms obj = handle_chat.chatroomslist.get(i);
                           String tempString = obj.name_of_chatroom+"("+obj.chatarraysockets.size()+")";
                           chatrooms_List.add(tempString);
                       }
                        Message m1 = new Message("getChatroomList", "server", "chatroomslist", username, chatrooms_List);
                        objoutputstrm.writeObject(m1);
                        objoutputstrm.flush();
                    }else if(msg.type.equals("join")){
                        Chatrooms askchatroom = null;
                       String askchatroomName=msg.content;
                       for(int i=0;i<handle_chat.chatroomslist.size();i++){
                           Chatrooms obj = handle_chat.chatroomslist.get(i);
                           if(obj.name_of_chatroom.equals(askchatroomName)){
                               askchatroom=(Chatrooms)obj;
                               break;
                           }
                       }
                       askchatroom.chatarraysockets.add(socket1);
                       askchatroom.users.add(username);
                       
                       try{
                           Message msg1 = new Message("join", "server", username, msg.content);
                           for(int i = 0;i<clientsockets.size();i++){
                               
                               Socket sock2 = (Socket)clientsockets.get(i);
                               ObjectOutputStream tobj = outputstream.get(i);
                               tobj.writeObject(msg1);
                               tobj.flush();
                           }
                       }catch(Exception e){
                           System.out.println(e);
                       }
                       
                    }else if(msg.type.equals("chat_user_list")){
                       Chatrooms askchatroom = null;
                       String askchatroomName=msg.content;
                       System.out.println(askchatroomName);
                       for(int i=0;i<handle_chat.chatroomslist.size();i++){
                           Chatrooms obj = handle_chat.chatroomslist.get(i);
                           if(obj.name_of_chatroom.equals(askchatroomName)){
                               System.out.println("found chatroom");
                               askchatroom=(Chatrooms)obj;
                               break;
                           }
                       }
                       objoutputstrm.writeObject(new Message("chat_user_list", "server", username, msg.content, askchatroom.users));
                       objoutputstrm.flush();
                       
                    }
                }
                    }finally{
                        socket1.close();
                    }
            }catch(Exception e){
            System.out.println("Server side exception:"+e);
        }
    }
    
}
