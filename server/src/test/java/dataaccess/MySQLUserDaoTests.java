package dataaccess;

import dataaccess.mysql.MySQLUserDao;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class MySQLUserDaoTests extends MySQLDaoTests {
    @BeforeEach
    public void setUp() {
        userDao = new MySQLUserDao();
        userDao.clear();
    }

    @AfterEach
    public void reset() {
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
    public void clearTest() {
        String[] usernames = {"user1", "user2", "user3"};
        userDao.createUser(new UserData(usernames[0], password, email));
        userDao.createUser(new UserData(usernames[1], password, email));
        userDao.createUser(new UserData(usernames[2], password, email));

        userDao.clear();

        assertTableEmpty(MySQLUserDao.userTableName);
    }
}
