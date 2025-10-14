import handlers.RegisterHandler;
import io.javalin.Javalin;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
    }

    public int run(int desiredPort) {
        createHandlers();

        javalin.start(desiredPort);

        return javalin.port();
    }

    private void createHandlers() {
        javalin.post("/user", new RegisterHandler(userService));
    }

    public void stop() {
        javalin.stop();
    }
}
