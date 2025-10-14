package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class LocalAuthTokenDao extends AuthTokenDao {
    HashMap<String, AuthData> authorizations = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authorizations.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authorizations.get(authToken);
    }

    @Override
    public void clear() {
        authorizations = new HashMap<>();
    }
}
