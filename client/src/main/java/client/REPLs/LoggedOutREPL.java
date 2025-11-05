package client.REPLs;

import model.UserData;
import model.requests.LoginRequest;
import server.ResponseException;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

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

    @Override
    protected String help() {
        System.out.print(SET_TEXT_COLOR_MAGENTA + "login <USERNAME> <PASSWORD> - ");
        System.out.println(SET_TEXT_COLOR_WHITE + " to access Chess functionality");

        System.out.print(SET_TEXT_COLOR_MAGENTA + "register <USERNAME> <PASSWORD> <EMAIL> - ");
        System.out.println(SET_TEXT_COLOR_WHITE + " to create an account");

        System.out.print(SET_TEXT_COLOR_MAGENTA + "help - ");
        System.out.println(SET_TEXT_COLOR_WHITE + " to display available commands");

        System.out.print(SET_TEXT_COLOR_MAGENTA + "quit - ");
        System.out.println(SET_TEXT_COLOR_WHITE + " to exit the program");

        return "help";
    }
}
