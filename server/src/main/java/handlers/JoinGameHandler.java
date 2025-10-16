package handlers;

import com.google.gson.Gson;
import handlers.requests.JoinGameRequest;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;

public class JoinGameHandler extends ChessHandler implements Handler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            String authToken = serializer.fromJson(ctx.header("authorization"), String.class);
            JoinGameRequest request = serializer.fromJson(ctx.body(), JoinGameRequest.class);
            request = request.withAuthToken(authToken);

            gameService.joinGame(request);
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\"}");
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\"}");
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json("{ \"message\": \"Error: already taken\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(String.format("{ \"message\": \"Error: %s\"}", e.getMessage()));
        }
    }
}