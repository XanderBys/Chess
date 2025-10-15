package dataaccess;

import model.AuthData;

public interface AuthTokenDao {
    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
