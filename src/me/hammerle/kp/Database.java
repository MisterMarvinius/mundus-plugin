package me.hammerle.kp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static Connection connection = null;
    private static String user;
    private static String password;
    private static int tries = 0;

    private static boolean connect() {
        try {
            tries++;
            if(tries > 10) {
                KajetansPlugin.warn("Too many database connect fails");
                return false;
            }
            if(connection != null) {
                connection.close();
            }
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost/minecraft?useSSL=false", user, password);
            KajetansPlugin.log("Connection to database etablished");
            tries--;
            return true;
        } catch(Exception ex) {
            KajetansPlugin.warn(ex.getMessage());
            return false;
        }
    }

    public static boolean connect(String user, String password) {
        Database.user = user;
        Database.password = password;
        return connect();
    }

    public static void close() {
        if(connection == null) {
            return;
        }
        try {
            connection.close();
            KajetansPlugin.log("Connection to database was closed");
        } catch(SQLException ex) {
            KajetansPlugin.warn(ex.getMessage());
        }
    }

    public static class SafeStatement implements AutoCloseable {
        private String query;
        private PreparedStatement statement;

        public SafeStatement(String query) throws SQLException {
            this.query = query;
            statement = connection.prepareStatement(query);
        }

        private void reconnect() throws SQLException {
            if(statement.isClosed() && connection.isClosed()) {
                connect();
                statement = connection.prepareStatement(query);
            }
        }

        @Override
        public void close() throws SQLException {
            statement.close();
        }

        public void setInt(int index, int i) throws SQLException {
            reconnect();
            statement.setInt(index, i);
        }

        public void setLong(int index, long l) throws SQLException {
            reconnect();
            statement.setLong(index, l);
        }

        public void setDouble(int index, double d) throws SQLException {
            reconnect();
            statement.setDouble(index, d);
        }

        public void setString(int index, String s) throws SQLException {
            reconnect();
            statement.setString(index, s);
        }

        public void setBoolean(int index, boolean b) throws SQLException {
            reconnect();
            statement.setBoolean(index, b);
        }

        public ResultSet executeQuery() throws SQLException {
            reconnect();
            return statement.executeQuery();
        }

        public void execute() throws SQLException {
            reconnect();
            statement.execute();
        }
    }

    public static SafeStatement prepare(String query) throws SQLException {
        return new SafeStatement(query);
    }
}
