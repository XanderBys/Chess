package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class LocalAuthTokenDao extends AuthTokenDao {
    HashSet<AuthData> authorizations = new HashSet<>();

    @Override
    public void createAuth(AuthData authData) {
        authorizations.add(authData);
    }
}
