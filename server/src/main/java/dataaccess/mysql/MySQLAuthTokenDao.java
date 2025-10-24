package dataaccess.mysql;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import service.UnauthorizedException;

import java.sql.SQLException;

public class MySQLAuthTokenDao implements AuthTokenDao {
    public static String authTableName = "auth";

    public MySQLAuthTokenDao() {
        DatabaseManager.createDatabase();
        DatabaseManager.createTable(String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    id INT NOT NULL AUTO_INCREMENT,
                    username VARCHAR(255) NOT NULL,
                    authToken CHAR(16),
                    PRIMARY KEY (id)
                );""", authTableName));
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String addAuthSQL = "INSERT INTO " + authTableName + " (username, authToken) VALUES(?,?)";
            try (var preparedStatement = conn.prepareStatement(addAuthSQL)) {
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to add auth session to database", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String addAuthSQL = "SELECT * FROM " + authTableName + " WHERE authToken=?";
            try (var preparedStatement = conn.prepareStatement(addAuthSQL)) {
                preparedStatement.setString(1, authToken);
                var rs = preparedStatement.executeQuery();
                rs.next();
                String username = rs.getString("username");
                return new AuthData(username, authToken);
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to get auth data", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            getAuth(authToken);
        } catch (DataAccessException e) {
            throw e;
        }

        try (var conn = DatabaseManager.getConnection()) {
            String addAuthSQL = "DELETE FROM " + authTableName + " WHERE authToken=?;";
            try (var preparedStatement = conn.prepareStatement(addAuthSQL)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to delete auth session", e);
        }
    }

    @Override
    public AuthData validateAuthData(String authToken) throws UnauthorizedException, DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String addAuthSQL = "DELETE FROM " + authTableName + ";";
            try (var preparedStatement = conn.prepareStatement(addAuthSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to clear auth table", e);
        }
    }
}
