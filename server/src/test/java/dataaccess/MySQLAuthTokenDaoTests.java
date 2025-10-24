package dataaccess;

import dataaccess.mysql.MySQLAuthTokenDao;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class MySQLAuthTokenDaoTests {
    private final String username = "xyz";
    private final String authToken = "1234567812345678";
    private final AuthData authData = new AuthData(username, authToken);
    private AuthTokenDao authDao;

    @BeforeEach
    public void setUp() {
        authDao = new MySQLAuthTokenDao();
        authDao.clear();
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
                    var id = rs.getInt("id");
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
}
