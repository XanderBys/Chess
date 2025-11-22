package ui.repls;

import model.AuthData;
import websocket.WebSocketFacade;

import java.util.HashMap;
import java.util.Scanner;

public class GameplayREPL extends REPL {
    private final WebSocketFacade wsFacade;
    private final AuthData authData;
    private final int gameID;

    public GameplayREPL(Scanner scanner, WebSocketFacade wsFacade, AuthData authData, int gameID) {
        this.authData = authData;
        this.gameID = gameID;
        this.scanner = scanner;
        this.wsFacade = wsFacade;
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
        // TODO: implement redrawBoard
        return "";
    }

    private String leave() {
        wsFacade.leave(authData.authToken(), gameID);
        return "leave";
    }

    private String makeMove(String[] params) {
        // TODO: implement makemove
        return "";
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
