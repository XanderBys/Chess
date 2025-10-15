package dataaccess;

import model.UserData;

public abstract class UserDao {
    public abstract UserData getUserData(String username);

    public abstract UserData getUserData(UserData userData);

    public abstract void createUser(UserData userData);

    public abstract void clear();
}
