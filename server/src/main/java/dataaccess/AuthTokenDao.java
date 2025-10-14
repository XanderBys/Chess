package dataaccess;

import model.AuthData;

public abstract class AuthTokenDao {
    public abstract void createAuth(AuthData authData);

    public abstract AuthData getAuth(String authToken);

    public abstract void clear();
}
