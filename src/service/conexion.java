packapackage service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/equipaya?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    @return 
    @throws
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("ERROR NO SE ENCUENTRA DATOS SQL " + e.getMessage(), e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
