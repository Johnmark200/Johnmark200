package Eclinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Eclinic.DBConnection;


public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/eclinic";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // <- Put your MySQL password here

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
