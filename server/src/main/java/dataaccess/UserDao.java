package dataaccess;

import model.UserData;

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
     * @param userData an instance of UserData containing the user's username, password, and email
     * @throws DataAccessException for database errors
     */
    void createUser(UserData userData) throws DataAccessException;

    /**
     * Clears all user data in the database 
     * @throws DataAccessException for database errors
     */
    void clear() throws DataAccessException;
}
