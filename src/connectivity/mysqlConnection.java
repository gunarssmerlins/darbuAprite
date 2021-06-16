package connectivity;

import java.sql.Connection;
import java.sql.DriverManager;

public class mysqlConnection {

    public Connection connection;

    public Connection getConnection(){

        String dbName = "orders";
        String userName = "gunars";
        String password = "Magnum4128";

//        String dbName = "orders";
//        String userName = "root";
//        String password = "Magnum41285127";

        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.168.1.148/"+dbName,userName,password);
//            connection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName,userName,password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
