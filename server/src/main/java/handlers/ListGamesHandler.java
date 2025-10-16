package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UnauthorizedException;

import java.util.Collection;

public class ListGamesHandler extends ChessHandler implements Handler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            Gson serializer = new Gson();
            String authToken = serializer.fromJson(ctx.header("authorization"), String.class);

            Collection<GameData> gameList = gameService.listGames(authToken);
            String serializedGameList = serializer.toJson(gameList);

            ctx.json(String.format("{\"games\": %s}", serializedGameList));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(String.format("{ \"message\": \"Error: %s\"}", e.getMessage()));
        }
    }
}