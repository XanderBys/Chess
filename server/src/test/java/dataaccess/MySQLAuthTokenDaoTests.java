package dataaccess;

import dataaccess.mysql.MySQLAuthTokenDao;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UnauthorizedException;

import java.sql.SQLException;

public class MySQLAuthTokenDaoTests extends MySQLDaoTests {
    @BeforeEach
    public void setUp() {
        authDao = new MySQLAuthTokenDao();
        authDao.clear();
    }

    @AfterEach
    public void takeDown() {
        authDao.clear();
    }

    @Test
    public void clearTest() {
        String[] usernames = {"user1", "user2", "user3"};
        authDao.createAuth(new AuthData(usernames[0], authToken));
        authDao.createAuth(new AuthData(usernames[1], authToken));
        authDao.createAuth(new AuthData(usernames[2], authToken));

        authDao.clear();

        assertTableEmpty(MySQLAuthTokenDao.authTableName);
    }

    @Test
    public void createAuthNormal() {
        authDao.createAuth(authData);

        try (var conn = DatabaseManager.getConnection()) {
            String getAuthSQL = "SELECT *  FROM " + MySQLAuthTokenDao.authTableName + " WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(getAuthSQL)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    var username = rs.getString("username");
                    var authToken = rs.getString("authToken");
                    Assertions.assertEquals(this.username, username);
                    Assertions.assertEquals(this.authToken, authToken);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("error accessing the database", e);
        }
    }

    @Test
    public void createAuthInvalidAuthToken() {
        Assertions.assertThrows(DataAccessException.class,
                () -> authDao.createAuth(new AuthData(username, "12345678123456789")));
    }

    @Test
    public void getAuthSessionNormal() {
        authDao.createAuth(authData);
        AuthData authDataFromDb = authDao.getAuth(authToken);

        Assertions.assertEquals(username, authDataFromDb.username());
        Assertions.assertEquals(authToken, authDataFromDb.authToken());
    }

    @Test
    public void getInvalidAuthSession() {
        Assertions.assertThrows(DataAccessException.class, () -> authDao.getAuth(authToken));
    }

    @Test
    public void deleteAuthSessionNormal() {
        authDao.createAuth(authData);
        authDao.deleteAuth(authToken);

        Assertions.assertThrows(DataAccessException.class, () -> authDao.getAuth(authToken));
    }

    @Test
    public void deleteInvalidAuthSession() {
        Assertions.assertThrows(DataAccessException.class, () -> authDao.deleteAuth(authToken));
    }

    @Test
    public void validateValidAuth() {
        authDao.createAuth(authData);
        var result = authDao.validateAuthData(authData.authToken());

        Assertions.assertEquals(authData, result);
    }

    @Test
    public void validateInvalidAuth() {
        Assertions.assertThrows(UnauthorizedException.class, () -> authDao.validateAuthData(authToken));
    }
}
