package server;

import dataaccess.*;
import handlers.ClearHandler;
import handlers.RegisterHandler;
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
        gameService = new GameService(gameDao);
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
    }

    public void stop() {
        javalin.stop();
    }
}
