package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameplayService;
import service.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final GameplayService gameplayService;

    public WebSocketHandler(GameplayService gameplayService) {
        this.gameplayService = gameplayService;
        gameplayService.setConnectionManager(connections);
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        ctx.enableAutomaticPings();
    }

    /**
     * Handles incoming messages
     *
     * @param ctx context variable from the WebSocket request
     * @throws Exception for IO errors with WebSocket
     */
    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws IOException {
        int gameID = -1;
        Session session = ctx.session;

        try {
            Gson serializer = new Gson();
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.gameID();
            String username = gameplayService.getUsernameFromAuthToken(command.authToken());
            connections.put(gameID, session);

            if (command.commandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                command = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
            }

            switch (command.commandType()) {
                case CONNECT -> gameplayService.connect(session, username, command);
                case MAKE_MOVE -> gameplayService.makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> gameplayService.leave(session, username, command);
                case RESIGN -> gameplayService.resign(session, username, command);
            }
        } catch (UnauthorizedException ex) {
            gameplayService.sendMessage(session, new ErrorMessage("Error: user not authorized"));
        } catch (Exception ex) {
            gameplayService.sendMessage(session, new ErrorMessage("An unexpected error has occurred."));
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
    }
}
