package server;

import dataaccess.*;
import handlers.*;
import io.javalin.Javalin;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
    }

    public Server() {
        this(null, null, null);

        UserDao userDao = new LocalUserDao();
        AuthTokenDao authDao = new LocalAuthTokenDao();
        GameDao gameDao = new LocalGameDao();

        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);
        clearService = new ClearService(userDao, authDao, gameDao);
    }

    public int run(int desiredPort) {
        createHandlers();

        javalin.start(desiredPort);

        return javalin.port();
    }

    private void createHandlers() {
        javalin.post("/user", new RegisterHandler(userService));

        javalin.delete("/db", new ClearHandler(clearService));

        javalin.post("/session", new LoginHandler(userService));
        javalin.delete("/session", new LogoutHandler(userService));

        javalin.post("/game", new CreateGameHandler(gameService));
        javalin.get("/game", new ListGamesHandler(gameService));
        javalin.put("/game", new JoinGameHandler(gameService));
    }

    public void stop() {
        javalin.stop();
    }
}
