package client.REPLs;

import server.ServerFacade;

import java.util.Scanner;

public class LoggedInREPL extends REPL {
    public LoggedInREPL(Scanner scanner, ServerFacade serverFacade) {
        this.scanner = scanner;
        this.serverFacade = serverFacade;
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
        // TODO: implement LoggedInREPL.createGame
        return "create";
    }

    private String logout() {
        // TODO: implement LoggedInREPL.logout
        return "logout";
    }

    @Override
    protected String help() {
        // TODO: implement help() in LoggedInREPL
        return "";
    }
}
