package client.REPLs;

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
            if (verifyLoginParameters(params)) {
                serverFacade.login(new LoginRequest(params[0], params[1]));
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

        return "login";
    }

    private boolean verifyLoginParameters(String[] params) {
        return params.length == 2 && !params[0].isEmpty() && !params[1].isEmpty();
    }

    private String register(String[] params) {
        // TODO: implement register
        return null;
    }

    private String help() {
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
