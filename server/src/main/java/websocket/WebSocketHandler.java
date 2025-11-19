package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import kotlin.NotImplementedError;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameplayService;
import service.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameplayService gameplayService;

    public WebSocketHandler(GameplayService gameplayService) {
        this.gameplayService = gameplayService;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        throw new NotImplementedError();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        int gameID = -1;
        Session session = ctx.session;

        try {
            Gson serializer = new Gson();
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.gameID();
            String username = gameplayService.getUsernameFromAuthToken(command.authToken());
            connections.put(gameID, session);

            switch (command.commandType()) {
                case CONNECT -> gameplayService.connect(session, username, command);
                case MAKE_MOVE -> gameplayService.makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> gameplayService.leave(session, username, command);
                case RESIGN -> gameplayService.resign(session, username, command);
            }
        } catch (UnauthorizedException ex) {
            throw new NotImplementedError();
        } catch (Exception ex) {
            throw new NotImplementedError();
        }
    }
}
