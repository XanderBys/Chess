package service;

import dataaccess.AuthTokenDao;
import dataaccess.GameDao;
import dataaccess.UserDao;

public class ClearService {
    UserDao userDao;
    AuthTokenDao authDao;
    GameDao gameDao;

    ClearService(UserDao userDao, AuthTokenDao authDao, GameDao gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }
}
