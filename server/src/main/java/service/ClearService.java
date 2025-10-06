package service;

import dataaccess.*;

public class ClearService {
    UserDao userDao = new LocalUserDao();
    AuthTokenDao authDao = new LocalAuthTokenDao();
    GameDao gameDao = new LocalGameDao();

    public void clear() {
    }
}
