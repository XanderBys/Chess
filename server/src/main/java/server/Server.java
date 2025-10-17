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

    /**
     * Creates a new instance of Server given instances of each of the service classes
     *
     * @param userService  an instance of UserService to be used by the server
     * @param gameService  an instance of GameService to be used by the server
     * @param clearService an instance of ClearService to be used by the server
     */
    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
    }

    /**
     * Creates an instance of Server without pre-instantiated versions of service classes.
     */
    public Server() {
        this(null, null, null);

        UserDao userDao = new LocalUserDao();
        AuthTokenDao authDao = new LocalAuthTokenDao();
        GameDao gameDao = new LocalGameDao();

        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);
        clearService = new ClearService(userDao, authDao, gameDao);
    }

    /**
     * Starts the server
     *
     * @param desiredPort the port for the server to run on. If 0, the server starts on any availabe port.
     * @return the port the server started on
     */
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
