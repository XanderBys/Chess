package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;

import java.sql.SQLException;

public abstract class MySQLDaoTests {
    protected final String username = "xyz";
    protected final String password = "securePassword1";
    protected final String email = "123@abc.com";
    protected final String authToken = "1234567812345678";

    protected final AuthData authData = new AuthData(username, authToken);
    protected final UserData userData = new UserData(username, password, email);
    protected final String gameName = "new game";

    protected AuthTokenDao authDao;
    protected UserDao userDao;
    protected GameDao gameDao;

    protected void assertTableEmpty(String tableName) {
        assertTableCount(tableName, 0);
    }

    protected void assertTableCount(String tableName, int count) {
        try (var conn = DatabaseManager.getConnection()) {
            String getAuthSQL = "SELECT COUNT(*) FROM " + tableName + ";";

            try (var preparedStatement = conn.prepareStatement(getAuthSQL)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                Assertions.assertEquals(count, rs.getInt("count(*)"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("error accessing the database", e);
        }
    }
}
