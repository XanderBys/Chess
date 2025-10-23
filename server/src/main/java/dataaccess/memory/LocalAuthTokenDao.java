package dataaccess.memory;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import model.AuthData;
import service.UnauthorizedException;

import java.util.HashMap;

public class LocalAuthTokenDao implements AuthTokenDao {
    HashMap<String, AuthData> authorizations = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authorizations.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authorizations.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authorizations.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        authorizations = new HashMap<>();
    }

    @Override
    public AuthData validateAuthData(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("");
        }
        return authData;
    }
}
