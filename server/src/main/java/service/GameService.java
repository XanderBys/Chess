package service;

import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import handlers.requests.CreateGameRequest;
import model.GameData;

import java.util.Collection;

import static util.ParameterValidation.validateString;

public class GameService {
    private final GameDao gameDao;
    private final AuthTokenDao authDao;

    public GameService(GameDao gameDao, AuthTokenDao authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public int createGame(CreateGameRequest request) throws UnauthorizedException, DataAccessException {
        String authToken = request.authToken();
        String gameName = request.gameName();
        validateString(authToken);
        validateString(gameName);

        authDao.validateAuthData(authToken);

        return gameDao.addGame(gameName);
    }

    public Collection<GameData> listGames(String authToken) {
        validateString(authToken);

        authDao.validateAuthData(authToken);

        return gameDao.listCurrentGames();
    }
}
