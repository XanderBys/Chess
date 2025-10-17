package handlers;

import com.google.gson.Gson;
import handlers.requests.CreateGameRequest;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;

public class CreateGameHandler implements Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            String authToken = ctx.header("authorization");
            CreateGameRequest newGame = serializer.fromJson(ctx.body(), CreateGameRequest.class);
            newGame = newGame.withAuthToken(authToken);

            int result = gameService.createGame(newGame);
            ctx.json(String.format("{ \"gameID\": %d}", result));
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