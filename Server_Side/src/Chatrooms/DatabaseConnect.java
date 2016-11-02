/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chatrooms;

import java.sql.*;
import java.lang.*;
import java.util.*;


public class DatabaseConnect {
    public Connection dbconn;
    DatabaseConnect(){
    try{
    dbconn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom","root","root123456");
    System.out.println("connection to db successful");
    Statement stmt = dbconn.createStatement();
    stmt.executeUpdate("use chatroom");
    System.out.println("using database chatroom");
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public boolean SignIn(String username, String password){
        try{
            
            Statement stmt = dbconn.createStatement();
            
            ResultSet rslt1 = stmt.executeQuery("select * from Userinfo where user_name ='" + username + "' and passwor='"+password+"'");
            if(rslt1.last()){
                System.out.println(rslt1.getString("user_name")+rslt1.getString("passwor"));
                return true;
            }else{
                return false;
            }
            
        }catch (Exception e){ 
            System.out.println(e);
        }
       return false;
    }
    
    public boolean SignUP(String username, String password){
    try{
        boolean b;
            
            b = check(username);
            
            if (b == false)
            {
                Statement stmt = (Statement) dbconn.createStatement(); 
                String insert = "INSERT INTO UserInfo(user_name,passwor) VALUES('" + username + "','" + password + "')";
                stmt.executeUpdate(insert); 
                return true;
            }
            else { return false; }
            
    }catch (Exception e){ System.out.println(e);
                   return false;}

    }

    private boolean check(String username) {
        try{
                
            Statement stmt = dbconn.createStatement();
            ResultSet rslt2 = stmt.executeQuery("select * from UserInfo where user_name='"+username+"'");
            System.out.println("result set:"+rslt2);
            if(rslt2.last()){
                System.out.println("data is available in the database");
                    return true;
            }else{
                System.out.print("data not available");
                return false;
            }
        }catch (Exception e){ 
            System.out.println(e);
        }
        return false;
    }
    public ArrayList<String> getListofChatrooms(){
    ArrayList<String> arraylist2 = new ArrayList<String>();
    try{
        //System.out.println("i am here");
        Statement stmt = (Statement)dbconn.createStatement();
        ResultSet rslt3 = stmt.executeQuery("Select * from chatroom");
        if(rslt3 !=null){
            //System.out.println(rslt3.);
            while(rslt3.next()){
                //System.out.println("i am here");
                arraylist2.add(rslt3.getString("chtrm_name"));
            }
            //System.out.println("i am here");
        }
    }catch(Exception e){
        }
    //System.out.println("i am here");
    return arraylist2;
    //System.out.println("i am here");
     // return null;
    }
    public boolean createChatRooms(String chatname){
     try{
         
         Statement stmt = (Statement) dbconn.createStatement();
         System.out.println("jo");
         String Insert = "Insert into chatroom(chtrm_name) values ('"+chatname+"')";
         stmt.executeUpdate(Insert);
         return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
}
    
