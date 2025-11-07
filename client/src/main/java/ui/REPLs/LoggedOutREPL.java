package ui.REPLs;

import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Scanner;

public class LoggedOutREPL extends REPL {
    public LoggedOutREPL(Scanner scanner, ServerFacade serverFacade) {
        this.scanner = scanner;
        this.serverFacade = serverFacade;
    }

    public void run() {
        run("LOGGED OUT", "quit");
    }

    @Override
    protected String evalInput(String input) {
        Command cmd = new Command(input);

        return switch (cmd.getName()) {
            case "login" -> login(cmd.getParams());
            case "register" -> register(cmd.getParams());
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String login(String[] params) {
        HashMap<Integer, String> errorMessages = new HashMap<>() {{
            put(400, "Either username or password is invalid");
            put(401, "Username and password not recognized");
        }};

        AuthData authData = (AuthData) executeServerFacadeAction("login",
                (String[] p) -> {
                    try {
                        return serverFacade.login(new LoginRequest(p[0], p[1]));
                    } catch (URISyntaxException | InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                params,
                2,
                "Successfully logged in!",
                errorMessages);

        if (authData != null) {
            new LoggedInREPL(scanner, serverFacade, authData).run();
            return "login";
        }

        return "";
    }

    private String register(String[] params) {
        HashMap<Integer, String> errorMessages = new HashMap<>() {{
            put(400, "There was an error processing your request. Please try again.");
            put(403, "Username already taken.");
        }};

        AuthData authData = (AuthData) executeServerFacadeAction("register",
                (String[] p) -> {
                    try {
                        return serverFacade.register(new UserData(p[0], p[1], p[2]));
                    } catch (URISyntaxException | InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                params,
                3,
                "Successfully registered!",
                errorMessages);

        if (authData != null) {
            return "register";
        }

        return "";
    }

    protected String help() {
        return super.help(new HashMap<>() {{
            put("login", new String[]{"to access Chess functionality", "username", "password"});
            put("register", new String[]{"to create an account", "username", "password", "email"});
            put("help", new String[]{"to display available commands"});
            put("quit", new String[]{"to exit the program"});
        }});
    }
}