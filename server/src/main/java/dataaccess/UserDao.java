package dataaccess;

import model.UserData;

public interface UserDao {
    UserData getUserData(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;
}
