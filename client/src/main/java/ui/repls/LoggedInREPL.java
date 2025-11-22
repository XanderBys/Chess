package ui.repls;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import ui.ChessBoardDrawer;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import static client.ChessClient.SERVER_URL;
import static ui.EscapeSequences.RESET_TEXT_UNDERLINE;
import static ui.EscapeSequences.SET_TEXT_UNDERLINE;

public class LoggedInREPL extends REPL {
    private final AuthData authData;
    private GameData[] currGameList;

    public LoggedInREPL(Scanner scanner, ServerFacade serverFacade, AuthData authData) {
        this.scanner = scanner;
        this.serverFacade = serverFacade;
        this.authData = authData;
    }

    public void run() {
        super.run("LOGGED IN", "logout");
    }

    @Override
    protected String evalInput(String input) {
        Command cmd = new Command(input);

        return switch (cmd.getName()) {
            case "logout" -> logout();
            case "create" -> createGame(cmd.getParams());
            case "list" -> listGames();
            case "join" -> joinGame(cmd.getParams());
            case "observe" -> observeGame(cmd.getParams());
            default -> help();
        };
    }

    /**
     * Allows a user to join a game as an observer. Prints an error if games have not yet been listed.
     *
     * @param params should contain 1 element: the number of the game to be observed
     * @return 'observe' if successful.
     */
    private String observeGame(String[] params) {
        GameData gameData = null;
        if (verifyParameters(1, params)) {
            gameData = getGameByListNumber(params[0]);
        } else {
            System.out.println("Observe requires exactly one parameter.");
        }

        if (gameData != null) {
            ChessBoardDrawer.drawBoard(gameData.game().getBoard(), ChessGame.TeamColor.WHITE);
        }
        return "observe";
    }

    /**
     * Allows a user to join a game
     * @param params should contain two elements: the number of the game to be joined and the color to be played
     * @return 'join'
     */
    private String joinGame(String[] params) {
        HashMap<Integer, String> errorMessages = new HashMap<>() {{
            put(400, "Please check command parameters and try again.");
            put(401, "There was an error authenticating you.");
            put(403,
                    "The game name and color you chose are already taken. Please choose a different game or color.");
        }};

        Object gameID = executeServerFacadeAction("join",
                (String[] p) -> {
                    try {
                        GameData gameData = getGameByListNumber(p[0]);
                        ChessGame.TeamColor color = getTeamColorFromInput(p[1]);
                        serverFacade.joinGame(color, gameData.gameID(), authData);
                        return gameData.gameID();
                    } catch (IOException | URISyntaxException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                params,
                2,
                "",
                errorMessages);

        if (gameID != null) {
            WebSocketFacade wsFacade = new WebSocketFacade(SERVER_URL, new NotificationHandler());
            new GameplayREPL(scanner, wsFacade, authData, (int) gameID).run();
        }

        return "join";
    }

    /**
     * Sanitizes user input to obtain a TeamColor
     * @param color a String, provided by the user, that is some variant of 'white' or 'black'
     * @return a TeamColor representing the user's input
     */
    private ChessGame.TeamColor getTeamColorFromInput(String color) {
        try {
            return ChessGame.TeamColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseException(500, "Player color must be either 'white' or 'black'");
        }
    }

    private GameData getGameByListNumber(String listNumber) {
        try {
            if (currGameList == null) {
                throw new ResponseException(500, "Please look at the list of games before joining one.");
            }

            if (listNumber.equals("0")) {
                throw new IndexOutOfBoundsException();
            }

            return currGameList[Integer.parseInt(listNumber)];

        } catch (IndexOutOfBoundsException e) {
            throw new ResponseException(500, "Game number " + listNumber + " not found.");
        } catch (NumberFormatException e) {
            throw new ResponseException(500, "Game number must be a positive integer.");
        }
    }

    /**
     * executes the list command
     * @return 'list' if successful
     */
    @SuppressWarnings("unchecked")
    private String listGames() {
        HashMap<Integer, String> errorMessages = new HashMap<>() {{
            put(401, "There was an error authenticating you.");
        }};

        Object gameList = executeServerFacadeAction("join",
                (String[] p) -> {
                    try {
                        return serverFacade.getGameList(authData);
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                new String[]{},
                0,
                "",
                errorMessages);

        if (gameList != null) {
            printGames((Collection<GameData>) gameList);
        }
        return "list";
    }

    private void printGames(Collection<GameData> gameList) {
        if (gameList.isEmpty()) {
            System.out.println("No current games.");
            return;
        }

        System.out.println(SET_TEXT_UNDERLINE + "Current game list:");
        System.out.print(RESET_TEXT_UNDERLINE);

        currGameList = new GameData[gameList.size() + 1];
        int i = 1;
        for (GameData game : gameList) {
            String whiteName = game.whiteUsername() == null ? "None" : game.whiteUsername();
            String blackName = game.blackUsername() == null ? "None" : game.blackUsername();
            System.out.println(
                    i + ") " + game.gameName() + ": WHITE=" + whiteName + " ; BLACK=" + blackName);
            currGameList[i] = game;
            i++;
        }
    }

    /**
     * Creates a new game.
     * @param params should contain 1 element: the name of the game to be created
     * @return 'create' if successful
     */
    private String createGame(String[] params) {
        HashMap<Integer, String> errorMessages = new HashMap<>() {{
            put(400, "Game name is invalid. Please try again.");
            put(401, "There was an error authenticating you.");
        }};

        executeServerFacadeAction("create",
                (String[] p) -> {
                    try {
                        return serverFacade.createGame(p[0], authData);
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                params,
                1,
                "New game with name " + params[0] + " successfully created!",
                errorMessages);

        return "create";
    }

    /**
     * Logs a user out
     * @return 'logout'
     */
    private String logout() {
        HashMap<Integer, String> errorMessages = new HashMap<>() {{
            put(401, "You are not authorized to perform that action. Please log in then try again.");
        }};

        executeServerFacadeAction("logout",
                (String[] p) -> {
                    try {
                        serverFacade.logout(authData);
                        return null;
                    } catch (IOException | InterruptedException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                },
                new String[]{},
                0,
                "Successfully logged out.",
                errorMessages);
        return "logout";
    }

    protected String help() {
        return super.help(new HashMap<>() {{
            put("logout", new String[]{"exit Chess session"});
            put("create", new String[]{"start a new game of chess", "game name"});
            put("list", new String[]{"list available chess games"});
            put("join", new String[]{"join an existing chess game", "game id", "player color"});
            put("observe", new String[]{"observe an existing chess game", "game id"});
            put("help", new String[]{"to display available commands"});
        }});
    }
}
