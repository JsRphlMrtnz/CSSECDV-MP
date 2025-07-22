/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Wesly
 */
public class Session {
    private int id;
    private String userId;
    private String token;
    private long timestamp;
    
    public Session(int id, String userId, String token, long timestamp){
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.timestamp = timestamp;
    }
    
    // Getters
    
    public int getId(){
        return id;
    }
    
    public String getUserId(){
        return userId;
    }
    
    public String getToken(){
        return token;
    }
    
    public long getTimestamp(){
        return timestamp;
    }
    
    
}
