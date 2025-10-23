package dataaccess.mysql;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import service.UnauthorizedException;

public class MySQLAuthTokenDao implements AuthTokenDao {
    public MySQLAuthTokenDao() {
        DatabaseManager.createTable("""
                CREATE TABLE IF NOT EXISTS auth (
                    id INT NOT NULL AUTO_INCREMENT,
                    username VARCHAR(255) NOT NULL,
                    authToken CHAR(16),
                    PRIMARY KEY (id)
                );""");
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public AuthData validateAuthData(String authToken) throws UnauthorizedException, DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
