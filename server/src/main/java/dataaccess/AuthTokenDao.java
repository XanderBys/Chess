package dataaccess;

import model.AuthData;
import service.UnauthorizedException;

public interface AuthTokenDao {
    /**
     * Adds a new instance of AuthData to the database
     *
     * @param authData instance of AuthData to be added
     * @throws DataAccessException for database errors
     */
    void createAuth(AuthData authData) throws DataAccessException;

    /**
     * Gets the instance of AuthData corresponding to a given auth token
     * @param authToken the desired auth token
     * @return an instance of AuthData corresponding to authToken, or null if there is no such instance
     * @throws DataAccessException for database errors
     */
    AuthData getAuth(String authToken) throws DataAccessException;

    /**
     * Removes an instance of AuthData from the database
     * @param authToken the auth token corresponding to the instance of AuthData to be removed
     * @throws DataAccessException for database errors
     */
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

    /**
     * Clears all auth sessions in the database
     * @throws DataAccessException for database errors
     */
    void clear() throws DataAccessException;
}
