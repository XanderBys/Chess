package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UnauthorizedException;
import service.UserService;

public class LogoutHandler extends ChessHandler implements Handler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            String authToken = serializer.fromJson(ctx.header("authorization"), String.class);

            userService.logout(authToken);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(String.format("{ \"message\": \"Error: %s\"}", e.getMessage()));
        }
    }
}