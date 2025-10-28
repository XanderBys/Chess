package service;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;

public class ClearService {
    UserDao userDao;
    AuthTokenDao authDao;
    GameDao gameDao;

    public ClearService(UserDao userDao, AuthTokenDao authDao, GameDao gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    /**
     * Deletes all data in each of the 3 DAOs
     *
     * @throws DataAccessException for internal data errors
     */
    public void clear() throws DataAccessException {
        try {
            userDao.clear();
            authDao.clear();
            gameDao.clear();
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }
}
