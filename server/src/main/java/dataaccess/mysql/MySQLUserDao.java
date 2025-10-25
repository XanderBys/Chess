package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDao;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySQLUserDao implements UserDao {
    public static String userTableName = "users";

    public MySQLUserDao() {
        DatabaseManager.createDatabase();
        DatabaseManager.createTable(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            id INT NOT NULL AUTO_INCREMENT,
                            username VARCHAR(%d) NOT NULL,
                            password VARCHAR(%d),
                            email VARCHAR(%d),
                            PRIMARY KEY (id));
                        """, userTableName, DatabaseManager.maxStringLength,
                DatabaseManager.maxStringLength, DatabaseManager.maxStringLength));
    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String addUserSQL = "INSERT INTO " + userTableName + " (username, password, email) VALUES (?, ?, ?);";
            try (var preparedStatement = conn.prepareStatement(addUserSQL)) {
                String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to create user", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.deleteTable(userTableName);
    }
}
