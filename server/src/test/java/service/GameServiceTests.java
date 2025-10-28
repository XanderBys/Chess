package service;

import chess.ChessGame;
import dataaccess.AuthTokenDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import dataaccess.memory.LocalAuthTokenDao;
import dataaccess.memory.LocalGameDao;
import dataaccess.memory.LocalUserDao;
import handlers.requests.CreateGameRequest;
import handlers.requests.JoinGameRequest;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class GameServiceTests {
    private final String username = "Xman";
    private GameDao gameDao;
    private GameService gameService;
    private String authToken;

    @BeforeEach
    public void setUp() {
        String password = "password";
        String email = "123@abc.com";

        AuthTokenDao authDao = new LocalAuthTokenDao();
        UserDao userDao = new LocalUserDao();
        gameDao = new LocalGameDao();

        gameService = new GameService(gameDao, authDao);
        UserService userService = new UserService(userDao, authDao);

        authToken = userService.register(new UserData(username, password, email)).authToken();
    }

    /**
     * positive test for createGame
     */
    @Test
    public void createGameNormal() {
        int gameId = gameService.createGame(new CreateGameRequest(authToken, "name"));

        Assertions.assertNotNull(gameDao.getGameDataById(gameId));
    }

    /**
     * negative test for createGame
     */
    @Test
    public void createGameUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.createGame(new CreateGameRequest(authToken + "a", "name")));
    }

    /**
     * positive test for listGames
     */
    @Test
    public void listGames() {
        gameService.createGame(new CreateGameRequest(authToken, "first game"));
        gameService.createGame(new CreateGameRequest(authToken, "second game"));

        Collection<GameData> games = gameService.listGames(authToken);
        Assertions.assertEquals(2, games.size());
        for (GameData data : games) {
            Assertions.assertNotNull(data.game());
            Assertions.assertTrue(data.gameName().equals("first game")
                    || data.gameName().equals("second game"));
        }
    }

    /**
     * negative test for listGames
     */
    @Test
    public void listGamesUnauthorized() {
        gameService.createGame(new CreateGameRequest(authToken, "first game"));
        gameService.createGame(new CreateGameRequest(authToken, "second game"));

        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames(authToken + "a"));
    }

    /**
     * negative test for listGames
     */
    @Test
    public void listGamesEmptyList() {
        Assertions.assertEquals(0, gameService.listGames(authToken).size());
    }

    /**
     * positive test for joinGame
     */
    @Test
    public void joinGameNormal() {
        int game1Id = gameService.createGame(new CreateGameRequest(authToken, "first game"));
        gameService.joinGame(new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, game1Id));

        Assertions.assertEquals(username, gameDao.getGameDataById(game1Id).whiteUsername());
    }

    /**
     * negative test for joinGame
     */
    @Test
    public void joinGameAlreadyTaken() {
        int game1Id = gameService.createGame(new CreateGameRequest(authToken, "first game"));
        gameService.joinGame(new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, game1Id));

        Assertions.assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, game1Id)));
    }
}
