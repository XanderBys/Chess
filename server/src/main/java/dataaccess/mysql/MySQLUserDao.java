package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDao;
import handlers.requests.LoginRequest;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.UnauthorizedException;

import java.sql.SQLException;

public class MySQLUserDao implements UserDao {
    public static String userTableName = "users";

    /**
     * Creates the database and a table to store user data if none exist
     */
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
        try (var conn = DatabaseManager.getConnection()) {
            String getUserSQL = "SELECT * FROM " + userTableName + " WHERE username=?;";
            try (var preparedStatement = conn.prepareStatement(getUserSQL)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    var hashedPassword = rs.getString("password");
                    var email = rs.getString("email");
                    return new UserData(username, hashedPassword, email);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to get user data", e);
        }
    }

    @Override
    public void validateUser(LoginRequest request) throws UnauthorizedException, DataAccessException {
        UserData result = getUserData(request.username());

        if (result == null || !BCrypt.checkpw(request.password(), result.password())) {
            throw new UnauthorizedException("username and password do not match");
        }
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
    public void clear() throws DataAccessException, SQLException {
        DatabaseManager.deleteTable(userTableName);
    }
}
