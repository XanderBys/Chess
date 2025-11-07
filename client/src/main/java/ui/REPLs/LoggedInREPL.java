package ui.REPLs;

import model.AuthData;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Scanner;

public class LoggedInREPL extends REPL {
    private final AuthData authData;

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

    private String observeGame(String[] params) {
        // TODO: implement LoggedInREPL.observeGame
        // TODO: implement Chess board rendering
        return "observe";
    }

    private String joinGame(String[] params) {
        // TODO: implement LoggedInREPL.joinGame
        return "join";
    }

    private String listGames() {
        // TODO: implement LoggedInREPL.listGames
        return "list";
    }

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
            put("join", new String[]{"join an existing chess game", "game id"});
            put("observe", new String[]{"observe an existing chess game", "game id"});
        }});
    }
}
