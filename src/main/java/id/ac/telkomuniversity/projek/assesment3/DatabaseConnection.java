package id.ac.telkomuniversity.projek.assesment3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public Connection getConnection() {
        Connection connection = null;
        try {
            String url = "jdbc:mariadb://127.0.0.1:3306/task_manager";
            String user = "root";
            String password = "ebi354";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
