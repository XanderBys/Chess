package ui.repls;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import ui.ChessBoardDrawer;
import websocket.WebSocketFacade;

import java.util.HashMap;
import java.util.Scanner;

public class GameplayREPL extends REPL {
    private static final HashMap<Character, Integer> rowLetterToNumber = new HashMap<>() {{
        put('a', 1);
        put('b', 2);
        put('c', 3);
        put('d', 4);
        put('e', 5);
        put('f', 6);
        put('g', 7);
        put('h', 8);
    }};

    private final WebSocketFacade wsFacade;
    private final AuthData authData;
    private final ChessGame.TeamColor teamColor;
    private final int gameId;
    private final ChessBoard mostRecentBoard;

    public GameplayREPL(Scanner scanner, WebSocketFacade wsFacade, AuthData authData,
                        ChessGame.TeamColor teamColor, GameData gameData) {
        this.authData = authData;
        this.gameId = gameData.gameID();
        this.scanner = scanner;
        this.wsFacade = wsFacade;
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
        int row = rowLetterToNumber.get(s.charAt(0));
        int col = s.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    private String resign() {
        // TODO: implement resign
        return "";
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
}
