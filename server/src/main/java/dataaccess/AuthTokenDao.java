package dataaccess;

import model.AuthData;

public abstract class AuthTokenDao {
    public abstract void createAuth(AuthData authData);

    public abstract void clear();
}
