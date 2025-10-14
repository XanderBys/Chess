package dataaccess;

import model.UserData;

import java.util.HashMap;

public class LocalUserDao extends UserDao {
    private static HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUserData(String username) {
        return LocalUserDao.users.get(username);
    }

    @Override
    public void createUser(UserData userData) {
        LocalUserDao.users.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        LocalUserDao.users = new HashMap<>();
    }
}
