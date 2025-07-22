package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import Model.Session;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class SQLite {
    
    public int DEBUG_MODE = 0;
    String driverURL = "jdbc:sqlite:" + "database.db";
    
    
    // Login Lockout Mechanism Constants
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    
    
    // Password Control Constants 
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;
    
    public void trackFailedLoginAttempts(Connection conn, String username){
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1, " +
                     "locked = CASE WHEN failed_login_attempts + 1 >= ? THEN 1 ELSE locked END " +
                     "WHERE username = ?";
        
         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, MAX_FAILED_LOGIN_ATTEMPTS);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void resetFailedLoginAttempts(Connection conn, String username) {
        String sql = "UPDATE users SET failed_login_attempts = 0 WHERE username = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
}
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " event TEXT NOT NULL,\n"
            + " username TEXT NOT NULL,\n"
            + " desc TEXT NOT NULL,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " name TEXT NOT NULL UNIQUE,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id TEXT PRIMARY KEY,\n"
            + " username TEXT NOT NULL UNIQUE,\n"
            + " password TEXT NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " locked INTEGER DEFAULT 0,\n"
            + " failed_login_attempts INTEGER DEFAULT 0\n" 
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void createSessionsTable(){
        String sql = "CREATE TABLE IF NOT EXISTS sessions (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " user_id TEXT NOT NULL,\n"        
            + " token TEXT NOT NULL UNIQUE,\n"   
            + " timestamp INTEGER NOT NULL,\n"   
            + " FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n"
            + ");";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table sessions in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addHistory(String username, String name, int stock, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,timestamp) VALUES('" + username + "','" + name + "','" + stock + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event,username,desc,timestamp) VALUES('" + event + "','" + username + "','" + desc + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name,stock,price) VALUES('" + name + "','" + stock + "','" + price + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public boolean addUser(String username, String password) {
        // Proper Password Strength Controls
        if(password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH){
            return false;
        }
        
        // userID UUID Generation
        String userId = UUID.randomUUID().toString();
        
        // Hash the password using a generated salt to create a unique hash.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Used PreparedStatement to prevent SQL injection
        String sql = "INSERT INTO users(id,username,password) VALUES(?,?,?)";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            pstmt.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.print(ex);
            return false;
        }
    }
    
    public boolean hasUser(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) // Existing User
                return true;
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return false;
    }
    
    public ArrayList<History> getHistory(){
        String sql = "SELECT id, username, name, stock, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return histories;
    }
    
    public ArrayList<Logs> getLogs(){
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                                   rs.getString("event"),
                                   rs.getString("username"),
                                   rs.getString("desc"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
    }
    
    public ArrayList<Product> getProduct(){
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return products;
    }
    
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role, locked, failed_login_attempts FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getString("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("locked"),
                                   rs.getInt("failed_login_attempts")));
            }
        } catch (Exception ex) {}
        return users;
    }
    
    public User login(String username, String password) {
        // Select username only
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            // If user is found
            if (rs.next()){
                int locked = rs.getInt("locked");
                String storedHash = rs.getString("password");
                
                if(locked == 1){
                    System.err.println("Login failed: Account for " + username + " is locked.");
                    return new User("", "", "", 0, 0, -1);
                }
            
                // Compare the provided password against the storedHash password from the database
                if(BCrypt.checkpw(password, storedHash)){
                    resetFailedLoginAttempts(conn, username);
                    return new User(rs.getString("id"),
                                   rs.getString("username"),
                                   storedHash,  // Return the hashed password instead of the plain password
                                   rs.getInt("role"),
                                   rs.getInt("locked"),
                                   rs.getInt("failed_login_attempts"));
                }else{
                    // Password Mismatch
                    trackFailedLoginAttempts(conn, username);
                    System.err.println("Login failed. AAAIncorrect Username/Password");
                    return null;
                }
            }else{
                // User not found
                System.err.println("Login failed. Incorrect Username/Password");
                return null;
            }
                
        } catch (Exception ex) {
            System.out.println("Login Error");
            System.out.print(ex);
        }
        return null;
    }
    
    public boolean addUser(String username, String password, int role) {
        
        // Proper Password Strength Controls
        if(password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH){
            return false;
        }
        
        // userID UUID Generation
        String userId = UUID.randomUUID().toString();
        
        
        
        // Hash the password using a generated salt to create a unique hash.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Used PreparedStatement to prevent SQL injection
        String sql = "INSERT INTO users(id,username,password,role,failed_login_attempts) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            pstmt.setInt(4, role);
            pstmt.setInt(5, 0);
            pstmt.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.print(ex);
            return false;
        }
    }
    
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username='" + username + "';";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public Product getProduct(String name){
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            product = new Product(rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price"));
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return product;
    }
    
    
    public void addSession(String userId, String token){
        String sql = "INSERT INTO sessions(user_id, token, timestamp) VALUES(?,?,?)";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, userId);
            pstmt.setString(2, token);
            pstmt.setLong(3, System.currentTimeMillis() / 1000);
            pstmt.executeUpdate();
            
            // Success
            System.out.println("Successfully added session into database."); 
        } catch (Exception ex) {
            System.out.print(ex);
        }   
    }
    
    public void deleteSession(String token){
        String sql = "DELETE FROM sessions WHERE token = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, token);
            pstmt.executeUpdate();
            
            // Success
            System.out.println("Successfully deleted session."); 
        }catch(Exception ex){
            System.out.println("Failed to delete session.");
            ex.printStackTrace();
        }
            
    }
    
    // Helpers
    
    public Session findSessionByToken(String token){
        String sql = "SELECT * FROM sessions WHERE token = ?";
         try(Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
             pstmt.setString(1, token);
             ResultSet rs = pstmt.executeQuery();
             
             if(rs.next()){
                 System.out.println("Session found!");
                 return new Session(
                     rs.getInt("id"),
                     rs.getString("user_id"),
                     rs.getString("token"),
                     rs.getLong("timestamp")
                 );
             }
         }catch(Exception ex){
             System.out.println("Failed to find session.");
             ex.printStackTrace();
         }
         
         return null;    
    }
    
    public User findUserById(String userId){
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try(Connection conn = DriverManager.getConnection(driverURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                String storedHash = rs.getString("password");
                System.out.println("User found!");
                return new User(rs.getString("id"),
                                   rs.getString("username"),
                                   storedHash,  // Return the hashed password instead of the plain password
                                   rs.getInt("role"),
                                   rs.getInt("locked"),
                                   rs.getInt("failed_login_attempts"));
            }         
        }catch(Exception ex){
            System.out.println("Failed to find user.");
            ex.printStackTrace();
        }
        
        return null;
    }
    
}