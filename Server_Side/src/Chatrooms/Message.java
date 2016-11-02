/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;

import java.io.*;
import java.util.*;

/**
 *
 * @author USER
 */
public class Message implements Serializable {
    public static final long serialVersionUID = 1L;
    public String type, sender, content, recipient;
    public ArrayList<String> dataList;
    
    
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    public Message(String type, String sender, String content, String recipient,ArrayList<String> tdataString){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;this.dataList=tdataString;
    }
    
    @Override
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"'}";
    }
    }

    
