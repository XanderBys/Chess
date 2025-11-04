package client.REPLs;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class LoggedOutREPL extends REPL {
    public LoggedOutREPL(Scanner scanner) {
        this.scanner = scanner;
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
        // TODO: implement login
        System.out.println("logging in...");
        return "";
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
