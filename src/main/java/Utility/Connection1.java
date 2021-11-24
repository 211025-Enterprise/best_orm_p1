package Utility;

import java.io.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connection1 {
    private static Properties properties;
    private static final String propPath = "C:\\Users\\levan\\Documents\\Project1_ORM\\src\\main\\resources\\app.properties";
//  load the app.properties file from resources to read in the url, username and password of the dabatase
    private static void loadProperties(){
        properties = new Properties();
        try{
            InputStream stream = new FileInputStream(new File(propPath).getAbsolutePath());
            properties.load(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//  to get the connection
    public static Connection getConnection(){
        if (properties == null) {
            loadProperties();
        }
        try
        {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("user"),
                    properties.getProperty("password")
            );
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
