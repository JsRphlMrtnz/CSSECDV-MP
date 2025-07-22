package Controller;


import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import Model.Session;
import View.Frame;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.io.File; 
import java.nio.file.Files; 
import java.nio.file.Path; 

public class Main {
    
    public SQLite sqlite;
    
    private static final long TOKEN_EXPIRATION_SECONDS = 60 * 1;
    
    private static final String SESSION_FILE = "session.dat";
    
    private User currentUser = null;
    
    private static final boolean initializeDatabase = false;
    
    public static void main(String[] args) {
        new Main().init();
    }
    
    public Main() {
        sqlite = new SQLite();
    }
    
    public void init(){
//         Initialize a driver object
        sqlite = new SQLite();

        
        if(initializeDatabase){
            System.out.println("DATABASE INITIALIZED.");
                    // Create a database
            sqlite.createNewDatabase();

            // Drop users table if needed
            sqlite.dropHistoryTable();
            sqlite.dropLogsTable();
            sqlite.dropProductTable();
            sqlite.dropUserTable();

            // Create users table if not exist
            sqlite.createHistoryTable();
            sqlite.createLogsTable();
            sqlite.createProductTable();
            sqlite.createUserTable();
            sqlite.createSessionsTable();

            // Add sample history
            sqlite.addHistory("admin", "Antivirus", 1, "2019-04-03 14:30:00.000");
            sqlite.addHistory("manager", "Firewall", 1, "2019-04-03 14:30:01.000");
            sqlite.addHistory("staff", "Scanner", 1, "2019-04-03 14:30:02.000");

            // Add sample logs
            sqlite.addLogs("NOTICE", "admin", "User creation successful", new Timestamp(new Date().getTime()).toString());
            sqlite.addLogs("NOTICE", "manager", "User creation successful", new Timestamp(new Date().getTime()).toString());
            sqlite.addLogs("NOTICE", "admin", "User creation successful", new Timestamp(new Date().getTime()).toString());

            // Add sample product
            sqlite.addProduct("Antivirus", 5, 500.0);
            sqlite.addProduct("Firewall", 3, 1000.0);
            sqlite.addProduct("Scanner", 10, 100.0);

            // Add sample users
            sqlite.addUser("admin", "qwerty1234" , 5);
            sqlite.addUser("manager", "qwerty1234", 4);
            sqlite.addUser("staff", "qwerty1234", 3);
            sqlite.addUser("client1", "qwerty1234", 2);
            sqlite.addUser("client2", "qwerty1234", 2);


    //        // Get users
    //        ArrayList<History> histories = sqlite.getHistory();
    //        for(int nCtr = 0; nCtr < histories.size(); nCtr++){
    //            System.out.println("===== History " + histories.get(nCtr).getId() + " =====");
    //            System.out.println(" Username: " + histories.get(nCtr).getUsername());
    //            System.out.println(" Name: " + histories.get(nCtr).getName());
    //            System.out.println(" Stock: " + histories.get(nCtr).getStock());
    //            System.out.println(" Timestamp: " + histories.get(nCtr).getTimestamp());
    //        }
    //        
    //        // Get users
    //        ArrayList<Logs> logs = sqlite.getLogs();
    //        for(int nCtr = 0; nCtr < logs.size(); nCtr++){
    //            System.out.println("===== Logs " + logs.get(nCtr).getId() + " =====");
    //            System.out.println(" Username: " + logs.get(nCtr).getEvent());
    //            System.out.println(" Password: " + logs.get(nCtr).getUsername());
    //            System.out.println(" Role: " + logs.get(nCtr).getDesc());
    //            System.out.println(" Timestamp: " + logs.get(nCtr).getTimestamp());
    //        }
    //        
    //        // Get users
    //        ArrayList<Product> products = sqlite.getProduct();
    //        for(int nCtr = 0; nCtr < products.size(); nCtr++){
    //            System.out.println("===== Product " + products.get(nCtr).getId() + " =====");
    //            System.out.println(" Name: " + products.get(nCtr).getName());
    //            System.out.println(" Stock: " + products.get(nCtr).getStock());
    //            System.out.println(" Price: " + products.get(nCtr).getPrice());
    //        }
    //        // Get users
            System.out.println("HEREEEE");
            ArrayList<User> users = sqlite.getUsers();
            System.out.println("User count: " + users.size());
            for(int nCtr = 0; nCtr < users.size(); nCtr++){
                System.out.println("===== User " + users.get(nCtr).getId() + " =====");
                System.out.println(" Username: " + users.get(nCtr).getUsername());
                System.out.println(" Password: " + users.get(nCtr).getPassword());
                System.out.println(" Role: " + users.get(nCtr).getRole());
                System.out.println(" Locked: " + users.get(nCtr).getLocked());
                System.out.println(" Login Attempts: " + users.get(nCtr).getNumLoginAttempts());
            }
        }

        
        // Call tryAutoLogin() here
        tryAutoLogin();
        
        // Initialize User Interface
        Frame frame = new Frame();
        frame.init(this);
        
        if (this.currentUser != null) {
            frame.mainNav(this.currentUser);
        }
    }
    
    private void tryAutoLogin(){
        File sessionFile = new File(SESSION_FILE);
        if(sessionFile.exists()){
            try{
                String token = Files.readString(Path.of(SESSION_FILE));
                Session session = sqlite.findSessionByToken(token);
                
                if(session != null){
                    long currentTime = System.currentTimeMillis() / 1000;
                    
                    // Check expiration status
                    if((currentTime - session.getTimestamp()) > TOKEN_EXPIRATION_SECONDS){
                        System.out.println("Session has expired. Please log in again.");
                        sqlite.deleteSession(token);
                        Files.delete(Path.of(SESSION_FILE));
                    }else{
                        User user = sqlite.findUserById(session.getUserId());
                        if(user != null && user.getLocked() == 0){
                            this.currentUser = user;
                            System.out.println("Successfully logged in user " + user.getUsername() + ". Session restored.");
                        }
                    }
                }
                
            }catch(Exception e){
                System.err.println("Could not process session file.");
                e.printStackTrace();
            }
        }
    }
    
    
    public boolean loginUser(String username, String password) {
        User user =  sqlite.login(username, password);
        if(user != null){
            setCurrentUser(user);
            
            String token = UUID.randomUUID().toString();
            
            sqlite.addSession(user.getId(), token);
            
            try{
                Files.writeString(Path.of(SESSION_FILE), token);
                System.out.println("[LOGIN] USER VALUE IS " + getCurrentUser().getUsername());
            }catch(Exception e){
                System.err.println("Could not write session file.");
                e.printStackTrace();
            }
            
            return true;   
        }
        return false;
    }
    
    public boolean logoutUser() {
        if (getCurrentUser() != null) {
            try{
                File sessionFile = new File(SESSION_FILE);
                
                if(sessionFile.exists()){
                    String token = Files.readString(Path.of(SESSION_FILE));
                    sqlite.deleteSession(token);
                    
                    Files.delete(sessionFile.toPath());          
                }
                
            }catch(Exception e){
                System.err.println("Error clearing persistent session: " + e.getMessage());
            }finally{
                setCurrentUser(null);
                System.out.println("User has been logged out.");
                return true;
            }      
        }
        System.out.println("CURRENT USER IS " + getCurrentUser());
        return false;
    }
    
    public boolean registerUser(String username, String password, String confirm) {
        if (sqlite.hasUser(username))
            return false;
        if (!password.equals(confirm))
            return false;
        return sqlite.addUser(username, password);
    }
    
    
    public User getCurrentUser(){
        return currentUser;
    }
    
    public void setCurrentUser(User user){
        this.currentUser = user;
    }
}
