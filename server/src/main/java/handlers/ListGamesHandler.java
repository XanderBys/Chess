package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.results.ListGamesResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UnauthorizedException;

public class ListGamesHandler implements Handler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            String authToken = ctx.header("authorization");

            ListGamesResult gameList = gameService.listGames(authToken);
            String serializedGameList = serializer.toJson(gameList);

            ctx.json(serializedGameList);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(String.format("{ \"message\": \"Error: %s\"}", e.getMessage()));
        }
    }
}