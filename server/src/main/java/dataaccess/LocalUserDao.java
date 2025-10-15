package dataaccess;

import model.UserData;

import java.util.HashMap;

public class LocalUserDao implements UserDao {
    HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }

    @Override
    public void clear() throws DataAccessException {
        users = new HashMap<>();
    }
}
