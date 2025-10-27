package dataaccess;

import dataaccess.mysql.MySQLUserDao;
import handlers.requests.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.UnauthorizedException;

import java.sql.SQLException;

public class MySQLUserDaoTests extends MySQLDaoTests {
    @BeforeEach
    public void setUp() throws SQLException {
        userDao = new MySQLUserDao();
        userDao.clear();
    }

    @AfterEach
    public void reset() throws SQLException {
        userDao.clear();
    }

    @Test
    public void addUserNormal() {
        userDao.createUser(userData);

        try (var conn = DatabaseManager.getConnection()) {
            String getAuthSQL = "SELECT *  FROM " + MySQLUserDao.userTableName + " WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(getAuthSQL)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                rs.next();
                var username = rs.getString("username");
                var email = rs.getString("email");
                Assertions.assertEquals(this.username, username);
                Assertions.assertEquals(this.email, email);
            }
        } catch (SQLException e) {
            throw new DataAccessException("error accessing the database", e);
        }
    }

    @Test
    public void addUserLongUsername() {
        Assertions.assertThrows(DataAccessException.class,
                () -> userDao.createUser(new UserData("a".repeat(256), password, email)));
    }

    @Test
    public void getUserNormal() {
        userDao.createUser(userData);
        UserData result = userDao.getUserData(username);
        Assertions.assertEquals(email, result.email());
        Assertions.assertEquals(username, result.username());
        Assertions.assertTrue(BCrypt.checkpw(password, result.password()));
    }

    @Test
    public void getInexistentUser() {
        Assertions.assertNull(userDao.getUserData(username));
    }

    @Test
    public void validateUserNormal() {
        userDao.createUser(userData);
        userDao.validateUser(new LoginRequest(username, password));
    }

    @Test
    public void validateUserBadPassword() {
        userDao.createUser(userData);
        Assertions.assertThrows(UnauthorizedException.class,
                () -> userDao.validateUser(new LoginRequest(username, "securePassword")));
    }

    @Test
    public void clearTest() throws SQLException {
        String[] usernames = {"user1", "user2", "user3"};
        userDao.createUser(new UserData(usernames[0], password, email));
        userDao.createUser(new UserData(usernames[1], password, email));
        userDao.createUser(new UserData(usernames[2], password, email));

        userDao.clear();

        assertTableEmpty(MySQLUserDao.userTableName);
    }
}
