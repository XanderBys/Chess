package handlers;

import com.google.gson.Gson;
import handlers.results.RegisterResult;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UserService;

public class RegisterHandler implements Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            UserData userData = serializer.fromJson(ctx.body(), UserData.class);

            RegisterResult result = userService.register(userData);
            ctx.json(serializer.toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json("{ \"message\": \"Error: already taken\"}");
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(String.format("{ \"message\": \"Error: %s\"}", e.getMessage()));
        }
    }
}