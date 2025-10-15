package dataaccess;

import model.AuthData;
import service.UnauthorizedException;

public interface AuthTokenDao {
    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    /**
     * Checks whether authToken is in the database
     *
     * @param authToken authToken to validate
     * @return an instance of AuthData
     * @throws UnauthorizedException if authToken not recognized
     * @throws DataAccessException   for internal data errors
     */
    AuthData validateAuthData(String authToken) throws UnauthorizedException, DataAccessException;

    void clear() throws DataAccessException;
}
