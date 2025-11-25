package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.ChessBoardDrawer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_BOLD;

public class NotificationHandler {
    private static final Gson deserializer = new Gson();

    public void notify(String msg) {
        ServerMessage serverMessage = deserializer.fromJson(msg, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> notifyLoadGameMessage(msg);
            case ERROR -> notifyErrorMessage(msg);
            case NOTIFICATION -> notifyNotificationMessage(msg);
        }
    }

    private void notifyLoadGameMessage(String msg) {
        LoadGameMessage serverMessage = deserializer.fromJson(msg, LoadGameMessage.class);
        ChessBoardDrawer.drawBoard(serverMessage.getGame().getBoard(), ChessGame.TeamColor.WHITE);
    }

    private void notifyErrorMessage(String msg) {
        ErrorMessage serverMessage = deserializer.fromJson(msg, ErrorMessage.class);
        System.out.println(SET_TEXT_BOLD + "ERROR: " + serverMessage.getMessage());
    }

    private void notifyNotificationMessage(String msg) {
        NotificationMessage serverMessage = deserializer.fromJson(msg, NotificationMessage.class);
        System.out.println(SET_TEXT_BOLD + serverMessage.getMessage());
    }
}