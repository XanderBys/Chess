package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.UserService;

public class RegisterHandler extends ChessHandler implements Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Gson serializer = new Gson();
        UserData userData = serializer.fromJson(ctx.body(), UserData.class);

        try {
            // TODO: add handling for bad request and server error
            RegisterResult result = userService.register(userData);
            ctx.result(serializer.toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(e.getMessage());
        }
    }
}