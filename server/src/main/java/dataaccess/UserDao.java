package dataaccess;

import model.UserData;

public interface UserDao {
    public abstract UserData getUserData(String username) throws DataAccessException;

    public abstract UserData getUserData(UserData userData) throws DataAccessException;

    public abstract void createUser(UserData userData) throws DataAccessException;

    public abstract void clear() throws DataAccessException;
}
