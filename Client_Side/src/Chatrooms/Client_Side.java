/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Client_Side implements Runnable{
    
     public Socket socket;
    public Login uinterface;
    public ObjectInputStream objinputstrm;
    public ObjectOutputStream objoutputstrm;
    public SignUP signUPuinterface;
    public SignIn signInuinterface;
    public String username;
    public Chatrms chtrms;
    public ArrayList<WindowChat> opened_chatroom;
    
    public Client_Side(Login frame) throws Exception{
        uinterface = frame;
        socket = new Socket("127.0.0.1", 8000); //169.254.119.98, 172.20.10.2
            
        objoutputstrm = new ObjectOutputStream(socket.getOutputStream());
        objoutputstrm.flush();
        objinputstrm = new ObjectInputStream(socket.getInputStream());
        opened_chatroom = new ArrayList<WindowChat>();
    }
    //run wala function aayega
    @Override
    public void run() {
        while(true){
            try{
                Message msg = (Message)objinputstrm.readObject();
                System.out.println("Received messsage:"+msg);
                if(msg.type.equals("connection")){
                    if(msg.content.equals("true")){
                        System.out.println("connection is successful");
                    }else{
                        System.out.println("something has went wrong");
                    }
                }else if(msg.type.equals("SignUP")){
                       System.out.println("Signup message"+msg);
                       if(msg.content.equals("true")){
                           username = msg.recipient;
                           signUPuinterface.ChatList(username);
                       }else{
                           signUPuinterface.showUsername_exists();
                       }
                       signUPuinterface=null;
               }else if(msg.type.equals("SignIn")){
                       System.out.println("Login message"+msg);
                       if(msg.content.equals("true")){
                           username = msg.recipient;
                           signInuinterface.ChatList(username);
                       }else{
                           signInuinterface.invalidCredentials();
                       }
                       signInuinterface=null;
               }else if(msg.type.equals("Create")){
                   System.out.println("after create beore true");
                   if(msg.content.equals("true")){
                       String creatorsname = msg.sender;
                       System.out.println("i m after creat us called");
                       getChatList(chtrms, new Message("getChatroomList", username, "getlist", "server"));
                       if(username.equals(creatorsname)){
                           joinChatRoom(chtrms, new Message("join", username, msg.recipient, "server"));
                       }else{
                           for (int i=0; i<opened_chatroom.size();i++) {
                               WindowChat obj = opened_chatroom.get(i);
                               obj.addamessage("Chatroom "+msg.recipient+" created");
                           }
                       }
                   }else{
                       JOptionPane.showMessageDialog(null, "Something has gone wrong . Please try again later.");
                }
                       
                       
                }else if(msg.type.equals("message")){
                    String sender = msg.sender;
                    String text = msg.content;
                    String chatroomname = msg.recipient;
                    for(int i=0;i<opened_chatroom.size();i++){
                        WindowChat obj = opened_chatroom.get(i);
                        if(obj.ChatWindowame.equals(chatroomname)){
                            if(username.equals(sender)){
                                obj.addamessage("You:"+text);
                            }else{
                                obj.addamessage(sender+":"+text);
                            }
                            break;
                        }
                    }
                }else if(msg.type.equals("leave")){
                   String name_of_chatroom = msg.recipient;
                   for(int i=0;i<opened_chatroom.size();i++){
                       WindowChat objj = (WindowChat)opened_chatroom.get(i);
                       if(objj.ChatWindowame.equals(name_of_chatroom)){
                           if(msg.content.equals(username)){
                               objj.dispose();
                               opened_chatroom.remove(objj);
                           }else{
                               objj.addamessage(msg.content+" left the chatroom");
                               objj.update_user_listOnleaving(msg.content);
                           }
                           
                           break;
                       }
                   }
                   getChatList(chtrms, new Message("getChatroomList", username, "getlist", "server"));
               }else if(msg.type.equals("logout")){
                       if(msg.content.equals("true")){
                           chtrms.dispose();
                           chtrms=null;
                           socket.close();
                           String[] args = {};
                           Login.main(args);
                           
                       }
               }else if(msg.type.equals("getChatroomList")){
                    System.out.println("got chatroomlist from server");
                   ArrayList<String> chatroomsname = msg.dataList;
                   chtrms.chatroom_ame_list(chatroomsname);
               }else if(msg.type.equals("join")){
                   
                   if(msg.content.equals(username)){
                       
                       System.out.println("Add chat window for this user because he is joining this chatroom for the first time.");
                       WindowChat chtwndow = new WindowChat(); // opens cht window of the requested chatrooom
                       chtwndow.ChatWindowame = msg.recipient;
                       chtwndow.customSetup(this,username);
                       chtwndow.setVisible(true);
                       opened_chatroom.add(chtwndow);
                       chtwndow.addamessage("You have joined");
                       send(new Message("chat_user_list", username, msg.recipient, "server"));
                   
                   }else{
                       //notify all the users that a respective user has joined the chat 
                       for(int i=0;i<opened_chatroom.size();i++){
                           WindowChat obj = opened_chatroom.get(i);
                           if(obj.ChatWindowame.equals(msg.recipient)){
                               obj.addamessage(msg.content+" has joined\n");
                               obj.update_user_listOnjoining(msg.content);
                               break;
                           }
                           
                       }
                   }
                   getChatList(chtrms, new Message("getChatroomList", username, "getlist", "server"));
               }else if(msg.type.equals("chat_user_list")){
                    System.out.println("List of users "+msg.dataList);
                   for(int i=0;i<opened_chatroom.size();i++){
                           WindowChat obj = opened_chatroom.get(i);
                           if(obj.ChatWindowame.equals(msg.recipient)){
                               obj.update(msg.dataList);
                               break;
                           }
                           
                       }
               }
                
            }catch(Exception e){
                
            }
        }
    }
    
    
    
    public void SignIn(SignIn frame, Message msg){
        signInuinterface=frame;
        send(msg);
    }
    
    public void SignUP(SignUP frame, Message msg){
        signUPuinterface=frame;
        //System.out.println("inside client side signup");
        send(msg);
    }
    
    public void logout(){
        for(int i=0;i<opened_chatroom.size();i++){
            WindowChat obj = (WindowChat)opened_chatroom.get(i);
            send(new Message("leave", username, obj.ChatWindowame, "server"));
        }
        send(new Message("logout", username, "turnoff", "server"));
    }
    
    public void getChatList(Chatrms chtrms1, Message msg){
        chtrms = chtrms1;
        send(msg);
    }
    
    public void joinChatRoom(Chatrms chtrms1, Message msg){
        boolean y=true;
        for(int i=0;i<opened_chatroom.size();i++){
            WindowChat obj = opened_chatroom.get(i);
            System.out.println(obj.ChatWindowame);
            if(obj.ChatWindowame.equals(msg.content)){
                System.err.println("it's open already");
                y=false;
                break;
            }
        }
        if(y){
            chtrms = chtrms1;
            send(msg);
        }else{
            JOptionPane.showMessageDialog(null, "Chatroom is already open !!!");
        }
    }
    
    public void send(Message msg){
        try {
            objoutputstrm.writeObject(msg);
            objoutputstrm.flush();
            System.out.println("Outgoing message : "+msg.toString());
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
}
