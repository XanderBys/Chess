package service;

import dataaccess.*;

public class ClearService {
    UserDao userDao;
    AuthTokenDao authDao;
    GameDao gameDao;

    public ClearService(boolean useLocalData) {
        if (useLocalData) {
            userDao = new LocalUserDao();
            authDao = new LocalAuthTokenDao();
            gameDao = new LocalGameDao();
        }
    }

    public ClearService() {
        this(true);
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }
}
