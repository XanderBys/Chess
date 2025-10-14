package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class LocalAuthTokenDao extends AuthTokenDao {
    private static HashSet<AuthData> authorizations = new HashSet<>();

    @Override
    public void createAuth(AuthData authData) {
        LocalAuthTokenDao.authorizations.add(authData);
    }

    @Override
    public void clear() {
        LocalAuthTokenDao.authorizations = new HashSet<>();
    }
}
