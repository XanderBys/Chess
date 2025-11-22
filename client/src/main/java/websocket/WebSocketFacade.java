package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import server.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private final Session session;
    private final NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            session.addMessageHandler(
                    (MessageHandler.Whole<String>)
                            s -> notificationHandler.notify(
                                    new Gson().fromJson(s, ServerMessage.class))
            );

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try {
            UserGameCommand leaveCommand = new UserGameCommand(
                    UserGameCommand.CommandType.LEAVE,
                    authToken,
                    gameID);
            session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
