package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import server.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private final Session session;

    /**
     * Initializes a WebSocket connection
     *
     * @param url                 the server url
     * @param notificationHandler a class to receive notifications and pass them to the user
     */
    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            session.addMessageHandler(
                    new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String s) {
                            notificationHandler.notify(
                                    s);
                        }
                    }
            );

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendUserGameCommand(UserGameCommand.CommandType commandType, String authToken, int gameID) {
        try {
            UserGameCommand cmd = new UserGameCommand(
                    commandType,
                    authToken,
                    gameID
            );
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(String authToken, int gameID) {
        sendUserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        sendUserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
    }

    public void makeMove(ChessMove move, String authToken, int gameID) {
        MakeMoveCommand moveCommand = new MakeMoveCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID,
                move
        );
        try {
            session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) {
        sendUserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
