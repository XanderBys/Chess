package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.UserData;

public class MySQLUserDao implements UserDao {
    @Override
    public UserData getUserData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
