package code; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    private static final String DB_NAME = "StudentDB"; 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME 
                                        + "?useSSL=false&allowPublicKeyRetrieval=true";
    
    private static final String USER = "root"; 
    private static final String PASS = "Pla1711@@@"; 
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName(DRIVER_CLASS);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Thiếu thư viện MySQL Connector J!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối đến Database!");
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}