package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UserService;

public class RegisterHandler extends ChessHandler implements Handler {
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
            ctx.result(serializer.toJson(result));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(e.getMessage());
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("Server error: " + e.getMessage());
        }
    }
}