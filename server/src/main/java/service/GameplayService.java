package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import dataaccess.AuthTokenDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class GameplayService {
    private final GameDao gameDao;
    private final AuthTokenDao authDao;

    private ConnectionManager connections = null;

    public GameplayService(AuthTokenDao authDao, GameDao gameDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public void setConnectionManager(ConnectionManager connections) {
        this.connections = connections;
    }

    /**
     * Connects a user to a chess game
     *
     * @param session  the WebSocket session
     * @param username of the user joining
     * @param cmd      the connect command
     * @throws IOException
     */
    public void connect(Session session, String username, UserGameCommand cmd) throws IOException {
        ChessGame game = gameDao.getGameDataById(cmd.gameID()).game();

        sendMessage(session, new LoadGameMessage(game));
        connections.broadcast(cmd.gameID(), session, new NotificationMessage(username + " has joined the game!"));
    }

    /**
     * Updates the board in the database according to the MakeMoveCommand
     * @param session the WebSocket session of the user's connection
     * @param username of the user making a move
     * @param cmd MakeMoveCommand containing the ChessMove and the ChessGame to update
     * @throws IOException for errors in WebSocket communication
     */
    public void makeMove(Session session, String username, MakeMoveCommand cmd) throws IOException {
        try {
            GameData gameData = gameDao.getGameDataById(cmd.gameID());
            ChessGame game = gameData.game();
            ChessMove move = cmd.getMove();
            validateMove(username, gameData, move);

            game.makeMove(move);
            gameDao.replaceGame(cmd.gameID(), gameData);
            connections.broadcast(cmd.gameID(), null, new LoadGameMessage(game));
            connections.broadcast(cmd.gameID(),
                    session,
                    new NotificationMessage(generateMoveDescription(username, game, move)));

            sendGameStateNotification(gameData);
        } catch (InvalidMoveException e) {
            sendMessage(session, new ErrorMessage("That move is invalid, please try a different move."));
        } catch (DataAccessException e) {
            sendMessage(session, new ErrorMessage("There was an internal database error. Please try again later."));
        }
    }

    /**
     * Checks whether the piece being moved belongs to the user moving it
     * @param username of the user making a move
     * @param data GameData of the game being played
     * @param move to be made
     * @throws InvalidMoveException if the piece being moved does not belong to the player moving it
     */
    private void validateMove(String username, GameData data, ChessMove move) throws InvalidMoveException {
        ChessGame game = data.game();
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());

        if (piece == null || !pieceColorMatchesUsername(piece, data, username)) {
            throw new InvalidMoveException();
        }
    }

    private boolean pieceColorMatchesUsername(ChessPiece piece, GameData data, String username) {
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        return (pieceColor.equals(ChessGame.TeamColor.WHITE) && data.whiteUsername().equals(username))
                || (pieceColor.equals(ChessGame.TeamColor.BLACK) && data.blackUsername().equals(username));
    }

    private String generateMoveDescription(String username, ChessGame game, ChessMove move) {
        ChessPiece piece = game.getBoard().getPiece(move.getEndPosition());
        return username + " moved their " + piece.getPieceType() + " to " + move.getEndPosition();
    }

    private void sendGameStateNotification(GameData gameData) throws IOException {
        String username = null;
        String state = null;
        if (gameData.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            username = gameData.whiteUsername();
            state = "stalemate";

        } else if (gameData.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
            username = gameData.blackUsername();
            state = "stalemate";
        }
        if (gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            username = gameData.whiteUsername();
            state = "checkmate";
        } else if (gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            username = gameData.blackUsername();
            state = "checkmate";
        } else if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            username = gameData.whiteUsername();
            state = "check";
        } else if (gameData.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            username = gameData.blackUsername();
            state = "check";
        }

        if (username != null) {
            connections.broadcast(gameData.gameID(),
                    null,
                    new NotificationMessage(username + " is in " + state + "!"));
        }
    }

    /**
     * Allows a user to leave a chess game
     * @param session WebSocket session of the user leaving
     * @param username of the user leaving
     * @param cmd UserGameCommand containing necessary information to leave
     * @throws IOException for WebSocket IO errors
     */
    public void leave(Session session, String username, UserGameCommand cmd) throws IOException {
        try {
            GameData gameData = gameDao.getGameDataById(cmd.gameID());

            if (username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername())) {
                gameData = gameData.removeUser(username);
                gameDao.replaceGame(cmd.gameID(), gameData);
            }

            connections.remove(cmd.gameID(), session);
            connections.broadcast(cmd.gameID(), null, new NotificationMessage(username + " has left the game."));
        } catch (DataAccessException e) {
            sendMessage(session, new ErrorMessage("There was an internal database error. Please try again later."));
        }
    }

    /**
     * Allows a user to resign from a game
     * @param session WebSocket session of the user resigning
     * @param username of the user resigning
     * @param cmd UserGameCommand for the resignation
     * @throws IOException for WebSocket IO errors
     */
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

            connections.broadcast(gameData.gameID(), null,
                    new NotificationMessage(username + " has resigned! Game over."));
        } catch (DataAccessException ex) {
            sendMessage(session, new ErrorMessage("There was an internal database error. Please try again later."));
        }
    }

    public String getUsernameFromAuthToken(String authToken) {
        return authDao.getAuth(authToken).username();
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(message.toString());
    }
}
