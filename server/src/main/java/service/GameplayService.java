package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthTokenDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import kotlin.NotImplementedError;
import org.eclipse.jetty.websocket.api.Session;
import websocket.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class GameplayService {
    private final GameDao gameDao;
    private final UserDao userDao;
    private final AuthTokenDao authDao;

    private ConnectionManager connections = null;

    public GameplayService(UserDao userDao, AuthTokenDao authDao, GameDao gameDao) {
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public void setConnectionManager(ConnectionManager connections) {
        this.connections = connections;
    }

    public void connect(Session session, String username, UserGameCommand cmd) throws IOException {
        ChessGame game = gameDao.getGameDataById(cmd.gameID()).game();

        sendMessage(session, cmd.gameID(), new LoadGameMessage(game));
        connections.broadcast(session, new NotificationMessage(username + " has joined the game!"));
    }

    public void makeMove(Session session, String username, MakeMoveCommand cmd) {
        throw new NotImplementedError();
    }

    public void leave(Session session, String username, UserGameCommand cmd) {
        throw new NotImplementedError();
    }

    public void resign(Session session, String username, UserGameCommand cmd) {
        throw new NotImplementedError();
    }

    public String getUsernameFromAuthToken(String authToken) {
        return authDao.getAuth(authToken).username();
    }

    private void sendMessage(Session session, int gameID, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
}
