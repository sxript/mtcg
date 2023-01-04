package db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Closeable;
import java.sql.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class DBConnection implements Closeable {
    private String dbName;
    private String username;
    private String password;
    private String port;
    private String jdbcURL;

    private static DBConnection instance;
    private Connection connection;

    private static final int DB_CONNECTION_RETRIES = 5;

    public DBConnection(String dbName, String username, String password, String port) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found");
            e.printStackTrace();
        }
        setDbName(dbName);
        setUsername(username);
        setPassword(password);
        setPort(port);
        setJdbcURL("jdbc:postgresql://localhost:" + getPort() + "/" + getDbName() + "");
    }

    public DBConnection() {
        this("mtcg", "postgres", "postgres", "5433");
    }

    public Connection connect() throws SQLException {
        DBRetryHandlerImpl retryHandler = new DBRetryHandlerImpl();
        while(true) {
            try {
                setConnection(DriverManager.getConnection(getJdbcURL(), getUsername(), getPassword()));
                System.out.println("CONNECTED TO DB");
            } catch (SQLException e) {
                retryHandler.exceptionOccurred(e,"DB Connection Failed");
                continue;
            }
            return getConnection();
        }
    }


    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = connect();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return connection;
    }


    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            connection = null;
        }
    }

    public static DBConnection getInstance() {
        if (instance == null)
            instance = new DBConnection();
        return instance;
    }

}
