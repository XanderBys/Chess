package dataaccess.mysql;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import model.AuthData;
import service.UnauthorizedException;

public class MySQLAuthTokenDao implements AuthTokenDao {
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
