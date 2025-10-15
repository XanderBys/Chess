package dataaccess;

import model.AuthData;

public interface AuthTokenDao {
    public abstract void createAuth(AuthData authData) throws DataAccessException;

    public abstract AuthData getAuth(String authToken) throws DataAccessException;

    public abstract void deleteAuth(String authToken) throws DataAccessException;

    public abstract void clear() throws DataAccessException;
}
