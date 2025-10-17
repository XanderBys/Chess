package handlers;

import com.google.gson.Gson;
import handlers.requests.LoginRequest;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;

public class LoginHandler implements Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            LoginRequest loginData = serializer.fromJson(ctx.body(), LoginRequest.class);

            AuthData result = userService.login(loginData);
            ctx.json(serializer.toJson(result));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\"}");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(String.format("{ \"message\": \"Error: %s\"}", e.getMessage()));
        }
    }
}