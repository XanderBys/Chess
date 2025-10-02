import handlers.RegisterHandler;
import io.javalin.Javalin;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        createHandlers();

        javalin.start(desiredPort);

        return javalin.port();
    }

    private void createHandlers() {
        javalin.post("/user", RegisterHandler::handleRegister);
    }

    public void stop() {
        javalin.stop();
    }
}
