package me.hammerle.kp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static Connection connection = null;

    public static boolean connect(String user, String password) {
        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost/minecraft?useSSL=false", user, password);
            KajetansPlugin.log("Connection to database etablished");
            KajetansPlugin.scheduleRepeatingTask(() -> {
                try(Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("SHOW TABLES IN minecraft");
                } catch(SQLException ex) {
                    KajetansPlugin.log("reconnect was done");
                }
            }, 100, 12000); // doing this every 10 minutes
            return true;
        } catch(Exception ex) {
            KajetansPlugin.log(ex.getMessage());
            return false;
        }
    }

    public static void close() {
        if(connection == null) {
            return;
        }
        try {
            connection.close();
            KajetansPlugin.log("Connection to database was closed");
        } catch(SQLException ex) {
            KajetansPlugin.log(ex.getMessage());
        }
    }

    public static PreparedStatement prepare(String query) throws SQLException {
        return connection.prepareStatement(query);
    }
}
