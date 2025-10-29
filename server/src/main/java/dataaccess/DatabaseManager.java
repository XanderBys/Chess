package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    public static int maxStringLength = 255;
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    /**
     * Executes the given SQL command to create a table
     *
     * @param createTableSQL SQL describing the table to be created
     * @throws DataAccessException for internal database errors
     */
    public static void createTable(String createTableSQL) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createTableSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to create table", e);
        }
    }

    /**
     * Deletes the table with the given name from the database
     * @param tableName the name of the table to be deleted
     * @throws SQLException if there is an error with the connection
     * @throws DataAccessException for other internal database errors
     */
    public static void deleteTable(String tableName) throws SQLException, DataAccessException {
        Connection connection = null;
        try (var conn = DatabaseManager.getConnection()) {
            connection = conn;
            connection.setAutoCommit(false);

            String deleteTableSQL = "DELETE FROM " + tableName + ";";
            try (var preparedStatement = conn.prepareStatement(deleteTableSQL)) {
                preparedStatement.executeUpdate();
            }

            String resetAutoincrementSQL = "ALTER TABLE " + tableName + " AUTO_INCREMENT = 0";
            try (var preparedStatement = conn.prepareStatement(resetAutoincrementSQL)) {
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
            throw new DataAccessException(String.format("unable to clear table '%s'", tableName), e);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
