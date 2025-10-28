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

    /**
     * creates a new game and stores it in the database
     *
     * @param request contains an auth token and game name (both strings)
     * @return the id of the created game
     * @throws UnauthorizedException if authToken is invalid
     * @throws DataAccessException   for internal errors
     */
    public int createGame(CreateGameRequest request) throws UnauthorizedException, DataAccessException {
        String authToken = request.authToken();
        String gameName = request.gameName();
        validateString(authToken);
        validateString(gameName);

        authDao.validateAuthData(authToken);

        return gameDao.addGame(gameName);
    }

    /**
     * Gets a list of all current games on the server
     *
     * @param authToken a String representing the user's session
     * @return a Collection of GameData instances representing all current games
     * @throws UnauthorizedException if authToken is invalid
     * @throws DataAccessException   for database errors
     */
    public Collection<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        validateString(authToken);

        authDao.validateAuthData(authToken);

        return gameDao.listCurrentGames();
    }

    /**
     * Adds a user to the requested game
     *
     * @param request object containing an auth token, the name of the color that the user will play, and the
     *                id of the game to be joined
     * @throws UnauthorizedException if request.authToken() is invalid
     * @throws DataAccessException   for database errors
     * @throws AlreadyTakenException if the requested color is already being played by a different user
     */
    public void joinGame(JoinGameRequest request)
            throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        validateString(request.authToken());
        validateTeamColor(request.playerColor());

        AuthData authData = authDao.validateAuthData(request.authToken());

        GameData gameData = gameDao.getGameDataById(request.gameID());
        if (gameData == null) {
            throw new BadRequestException(String.format("Game with id %d does not exist", request.gameID()));
        }

        checkIfPlayerColorIsOccupied(request.playerColor(), gameData);

        gameDao.replaceGame(request.gameID(), gameData.addUser(request.playerColor(), authData.username()));
    }

    /**
     * Checks if the given color is occupied
     *
     * @param color    the TeamColor that was requested
     * @param gameData the data for the game being played
     * @throws AlreadyTakenException if 'color' is already being played by another user
     */
    private void checkIfPlayerColorIsOccupied(ChessGame.TeamColor color, GameData gameData) throws AlreadyTakenException {
        if (color == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
            throw new AlreadyTakenException("White is already taken");
        } else if (color == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
            throw new AlreadyTakenException("Black is already taken");
        }
    }


}
