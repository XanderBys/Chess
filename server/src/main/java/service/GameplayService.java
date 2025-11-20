package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class GameplayService {
    private static final Logger log = LoggerFactory.getLogger(GameplayService.class);
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

        sendMessage(session, new LoadGameMessage(game));
        connections.broadcast(session, new NotificationMessage(username + " has joined the game!"));
    }

    public void makeMove(Session session, String username, MakeMoveCommand cmd) throws IOException {
        try {
            GameData gameData = gameDao.getGameDataById(cmd.gameID());
            ChessGame game = gameData.game();
            ChessMove move = cmd.getMove();
            validateMove(username, gameData, move);

            game.makeMove(move);
            gameDao.replaceGame(cmd.gameID(), gameData);
            connections.broadcast(null, new LoadGameMessage(game));
            connections.broadcast(session, new NotificationMessage(generateMoveDescription(username, game, move)));

            sendGameStateNotification(gameData);
        } catch (InvalidMoveException e) {
            sendMessage(session, new ErrorMessage("That move is invalid, please try a different move."));
        } catch (DataAccessException e) {
            sendMessage(session, new ErrorMessage("There was an internal database error. Please try again later."));
        }
    }

    private void validateMove(String username, GameData data, ChessMove move) throws InvalidMoveException {
        ChessGame game = data.game();
        ChessGame.TeamColor pieceColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();
        if (!pieceColorMatchesUsername(pieceColor, data, username)) {
            throw new InvalidMoveException();
        }
    }

    private boolean pieceColorMatchesUsername(ChessGame.TeamColor pieceColor, GameData data, String username) {
        return (pieceColor.equals(ChessGame.TeamColor.WHITE) && data.whiteUsername().equals(username))
                || (pieceColor.equals(ChessGame.TeamColor.BLACK) && data.blackUsername().equals(username));
    }

    private String generateMoveDescription(String username, ChessGame game, ChessMove move) {
        ChessPiece piece = game.getBoard().getPiece(move.getEndPosition());
        return username + " moved their " + piece.getPieceType() + " to " + move.getEndPosition();
    }

    private void sendGameStateNotification(GameData gameData) throws IOException {
        // TODO: Make code less repetitive
        if (gameData.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            connections.broadcast(null,
                    new NotificationMessage(gameData.whiteUsername() + " is in stalemate! Game over."));
        } else if (gameData.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
            connections.broadcast(null,
                    new NotificationMessage(gameData.blackUsername() + " is in stalemate! Game over."));
        }
        if (gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            connections.broadcast(null,
                    new NotificationMessage(gameData.whiteUsername() + " is in checkmate! Game over."));
        } else if (gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            connections.broadcast(null,
                    new NotificationMessage(gameData.blackUsername() + " is in checkmate! Game over."));
        } else if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            connections.broadcast(null,
                    new NotificationMessage(gameData.whiteUsername() + " is in check!"));
        } else if (gameData.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            connections.broadcast(null,
                    new NotificationMessage(gameData.blackUsername() + " is in check!"));
        }
    }

    public void leave(Session session, String username, UserGameCommand cmd) throws IOException {
        try {
            GameData gameData = gameDao.getGameDataById(cmd.gameID());

            if (username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername())) {
                gameData = gameData.removeUser(username);
                gameDao.replaceGame(cmd.gameID(), gameData);
            }

            connections.remove(cmd.gameID(), session);
            connections.broadcast(null, new NotificationMessage(username + " has left the game."));
        } catch (DataAccessException e) {
            sendMessage(session, new ErrorMessage("There was an internal database error. Please try again later."));
        }
    }

    public void resign(Session session, String username, UserGameCommand cmd) throws IOException {
        try {
            GameData gameData = gameDao.getGameDataById(cmd.gameID());

            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                sendMessage(session, new ErrorMessage("Observers do not have permission to resign."));
                return;
            }

            ChessGame game = gameData.game();

            if (game.isGameOver()) {
                sendMessage(session, new ErrorMessage("You cannot resign a game that is already over."));
                return;
            }

            game.setGameOver(true);
            gameDao.replaceGame(gameData.gameID(), gameData);

            connections.broadcast(null,
                    new NotificationMessage(username + " has resigned! Game over."));
        } catch (DataAccessException ex) {
            sendMessage(session, new ErrorMessage("There was an internal database error. Please try again later."));
        }
    }

    public String getUsernameFromAuthToken(String authToken) {
        return authDao.getAuth(authToken).username();
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
}
