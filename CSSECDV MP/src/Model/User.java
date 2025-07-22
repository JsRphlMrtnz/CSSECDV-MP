package Model;

public class User {
    private String id;
    private String username;
    private String password;
    private int role = 2;
    private int locked = 0;
    private int numLoginAttempts = 0;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public User(String id, String username, String password, int role, int locked, int numLoginAttempts){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.locked = locked;
        this.numLoginAttempts = numLoginAttempts;
    }
    
    // Getters
    
    public String getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }    
    
    public String getPassword() {
        return password;
    }
    
    public int getRole() {
        return role;
    }
    
    public int getLocked() {
        return locked;
    }

    public int getNumLoginAttempts(){
        return numLoginAttempts;
    }
    
    // Setters
    
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }
    
    public void setNumLoginAttempts(int numLoginAttempts){
        this.numLoginAttempts = numLoginAttempts;
    }
    
}
