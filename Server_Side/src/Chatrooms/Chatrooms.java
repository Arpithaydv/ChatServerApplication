/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;

import java.net.*;
import java.io.*;
import java.util.*;


public class Chatrooms {
    public ArrayList<Socket> chatarraysockets;
    public ArrayList<String> users;
    public String name_of_chatroom; 
    //boolean chatname;
    
    Chatrooms(String cname){
        users = new ArrayList<String>();
        chatarraysockets = new ArrayList<Socket>();
        name_of_chatroom = cname;
    }
}
