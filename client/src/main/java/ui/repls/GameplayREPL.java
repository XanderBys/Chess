package ui.repls;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import ui.ChessBoardDrawer;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.Scanner;

import static client.ChessClient.SERVER_URL;
import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_BOLD;

public class GameplayREPL extends REPL implements NotificationHandler {
    private static final HashMap<Character, Integer> colLetterToNumber = new HashMap<>() {{
        put('a', 1);
        put('b', 2);
        put('c', 3);
        put('d', 4);
        put('e', 5);
        put('f', 6);
        put('g', 7);
        put('h', 8);
    }};

    private static final Gson deserializer = new Gson();
    
    private final AuthData authData;
    private final ChessGame.TeamColor teamColor;
    private final int gameId;
    private final WebSocketFacade wsFacade;
    private ChessBoard mostRecentBoard;

    public GameplayREPL(Scanner scanner, AuthData authData,
                        ChessGame.TeamColor teamColor, GameData gameData) {
        this.authData = authData;
        this.gameId = gameData.gameID();
        this.scanner = scanner;
        this.wsFacade = new WebSocketFacade(SERVER_URL, this);
        this.teamColor = teamColor;
        this.mostRecentBoard = gameData.game().getBoard();

        redrawBoard();
    }

    public void run() {
        super.run("GAMEPLAY", "leave");
    }

    @Override
    protected String evalInput(String input) {
        Command cmd = new Command(input);
        return switch (cmd.getName()) {
            case "redraw" -> redrawBoard();
            case "leave" -> leave();
            case "move" -> makeMove(cmd.getParams());
            case "resign" -> resign();
            case "show_moves" -> highlightLegalMoves();
            default -> help();
        };
    }

    private String redrawBoard() {
        System.out.print(RESET_TEXT_BOLD_FAINT);
        ChessBoardDrawer.drawBoard(mostRecentBoard, teamColor);
        return "redraw";
    }

    private String leave() {
        wsFacade.leave(authData.authToken(), gameId);
        return "leave";
    }

    private String makeMove(String[] params) {
        ChessPosition fromPos = getChessPositionFromString(params[0]);
        ChessPosition toPos = getChessPositionFromString(params[1]);
        wsFacade.makeMove(new ChessMove(fromPos, toPos), authData.authToken(), gameId);

        return "makeMove";
    }

    private ChessPosition getChessPositionFromString(String s) {
        int col = colLetterToNumber.get(s.charAt(0));
        int row = s.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    private String resign() {
        wsFacade.resign(authData.authToken(), gameId);
        return "resign";
    }

    private String highlightLegalMoves() {
        // TODO: implement highlightLegalMoves
        return "";
    }

    protected String help() {
        return super.help(new HashMap<>() {{
            put("help", new String[]{"to display available commands"});
            put("redraw", new String[]{"the current chess board"});
            put("leave", new String[]{"the current chess game"});
            put("move", new String[]{"make a move",
                    "start square",
                    "end square"});
            put("resign", new String[]{"forfeit the chess game"});
            put("show_moves", new String[]{"show the legal moves for the piece on a given square", "location"});
        }});
    }

    private void printPrompt() {
        super.printPrompt("GAMEPLAY");
    }

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
        mostRecentBoard = serverMessage.getGame().getBoard();
        System.out.println();
        redrawBoard();
        printPrompt();
    }

    private void notifyErrorMessage(String msg) {
        ErrorMessage serverMessage = deserializer.fromJson(msg, ErrorMessage.class);
        System.out.println(SET_TEXT_BOLD + "ERROR: " + serverMessage.getMessage());
        System.out.print(RESET_TEXT_BOLD_FAINT);
        printPrompt();
    }

    private void notifyNotificationMessage(String msg) {
        NotificationMessage serverMessage = deserializer.fromJson(msg, NotificationMessage.class);
        System.out.println(SET_TEXT_BOLD + serverMessage.getMessage());
        System.out.print(RESET_TEXT_BOLD_FAINT);
        printPrompt();
    }
}
