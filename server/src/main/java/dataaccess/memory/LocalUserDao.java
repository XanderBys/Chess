package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.UserData;
import model.requests.LoginRequest;
import service.UnauthorizedException;

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
    public void validateUser(LoginRequest request) throws UnauthorizedException, DataAccessException {
        UserData storedUserData = getUserData(request.username());

        if (storedUserData == null || !storedUserData.password().equals(request.password())) {
            throw new UnauthorizedException("");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        users = new HashMap<>();
    }
}
