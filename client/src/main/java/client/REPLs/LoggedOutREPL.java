package client.REPLs;

import model.UserData;
import model.requests.LoginRequest;
import server.ResponseException;
import server.ServerFacade;

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
        try {
            if (verifyParameters(2, params)) {
                serverFacade.login(new LoginRequest(params[0], params[1]));
                System.out.println("Successfully logged in!");
                new LoggedInREPL(scanner, serverFacade).run();
                return "login";
            } else {
                System.out.println("You must provide both username and password");
            }
        } catch (ResponseException e) {
            switch (e.getErrorCode()) {
                case 400 -> System.out.println("Either username or password is invalid");
                case 401 -> System.out.println("Username and password not recognized");
                default -> System.out.println("There was an error in processing your login");
            }
        } catch (Exception e) {
            System.out.println("It's not possible to log you in right now. Please try again later.");
        }

        return "";
    }

    private String register(String[] params) {
        try {
            if (verifyParameters(3, params)) {
                serverFacade.register(new UserData(params[0], params[1], params[2]));
                System.out.println("Successfully registered! Please log in to continue.");
                return "register";
            } else {
                System.out.println("Username, password, and email are all required to register. Please try again.");
            }
        } catch (ResponseException e) {
            switch (e.getErrorCode()) {
                case 400 -> System.out.println("There was an error processing your request. Please try again.");
                case 403 -> System.out.println("Username already taken.");
                default -> System.out.println("There was an error in processing your login.");
            }
        } catch (Exception e) {
            System.out.println("It's not possible to register right now. Please try again later.");
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