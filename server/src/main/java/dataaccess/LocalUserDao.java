package dataaccess;

import model.UserData;

import java.util.HashMap;

public class LocalUserDao extends UserDao {
    HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUserData(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        users = new HashMap<>();
    }
}
