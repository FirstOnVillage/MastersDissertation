package sample;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import javax.swing.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQLConnect
{
    private static Connection connection = null;
    private static final  String URL = "jdbc:mysql://localhost:3306/dbrecipeselection";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    public static void ConnectDB()
    {
        try
        {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Ошибка подключения БД", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void CloseConnect() throws SQLException
    {
        connection.close();
    }
    public static Connection getConnection()
    {
        return connection;
    }
}
