package dataaccess;

import model.UserData;
import model.requests.LoginRequest;
import service.UnauthorizedException;

import java.sql.SQLException;

public interface UserDao {
    /**
     * Gets a user's data by username
     *
     * @param username the username of the desired user
     * @return an instance of UserData corresponding to the username
     * @throws DataAccessException for database errors
     */
    UserData getUserData(String username) throws DataAccessException;

    /**
     * Creates a new user in the database
     *
     * @param userData an instance of UserData containing the user's username, password, and email
     * @throws DataAccessException for database errors
     */
    void createUser(UserData userData) throws DataAccessException;

    /**
     * Checks that user is registered and that username and password match
     *
     * @param request an instance of LoginRequest containing username and password
     * @throws UnauthorizedException if the password doesn't match username or if username is null
     * @throws DataAccessException   for internal data errors
     */
    void validateUser(LoginRequest request) throws UnauthorizedException, DataAccessException;

    /**
     * Clears all user data in the database
     *
     * @throws DataAccessException for database errors
     */
    void clear() throws DataAccessException, SQLException;
}
