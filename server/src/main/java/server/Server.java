package server;

import dataaccess.AuthTokenDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import dataaccess.memory.LocalAuthTokenDao;
import dataaccess.memory.LocalGameDao;
import dataaccess.memory.LocalUserDao;
import dataaccess.mysql.MySQLAuthTokenDao;
import dataaccess.mysql.MySQLGameDao;
import dataaccess.mysql.MySQLUserDao;
import handlers.*;
import io.javalin.Javalin;
import service.ClearService;
import service.GameService;
import service.GameplayService;
import service.UserService;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final GameplayService gameplayService;

    /**
     * Creates an instance of Server without pre-instantiated versions of service classes.
     *
     * @param useMySql if true, uses MySQL to store server data. If false, stores data locally in memory
     */
    public Server(boolean useMySql) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserDao userDao;
        AuthTokenDao authDao;
        GameDao gameDao;
        if (useMySql) {
            userDao = new MySQLUserDao();
            authDao = new MySQLAuthTokenDao();
            gameDao = new MySQLGameDao();
        } else {
            userDao = new LocalUserDao();
            authDao = new LocalAuthTokenDao();
            gameDao = new LocalGameDao();
        }

        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);
        clearService = new ClearService(userDao, authDao, gameDao);
        gameplayService = new GameplayService(authDao, gameDao);
    }

    public Server() {
        this(true);
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

        WebSocketHandler wsHandler = new WebSocketHandler(gameplayService);
        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
            ws.onMessage(wsHandler);
            ws.onClose(wsHandler);
        });
    }

    public void stop() {
        javalin.stop();
    }
}
