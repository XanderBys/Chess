package service;

import chess.ChessGame;
import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import handlers.requests.CreateGameRequest;
import handlers.requests.JoinGameRequest;
import model.AuthData;
import model.GameData;

import java.util.Collection;

import static util.ParameterValidation.validateString;
import static util.ParameterValidation.validateTeamColor;

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

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        validateString(authToken);

        authDao.validateAuthData(authToken);

        return gameDao.listCurrentGames();
    }

    public void joinGame(JoinGameRequest request)
            throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        validateString(request.authToken());
        validateTeamColor(request.playerColor());

        AuthData authData = authDao.validateAuthData(request.authToken());

        GameData gameData = gameDao.getGameDataById(request.gameID());
        if (gameData == null) {
            throw new BadRequestException(String.format("Game with id %d does not exist", request.gameID()));
        }

        checkIfPlayerColorIsOccupied(request, gameData);

        gameDao.replaceGame(request.gameID(), gameData.addUser(request.playerColor(), authData.username()));
    }

    private void checkIfPlayerColorIsOccupied(JoinGameRequest request, GameData gameData) throws AlreadyTakenException {
        if (request.playerColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
            throw new AlreadyTakenException("White is already taken");
        } else if (request.playerColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
            throw new AlreadyTakenException("Black is already taken");
        }
    }


}
