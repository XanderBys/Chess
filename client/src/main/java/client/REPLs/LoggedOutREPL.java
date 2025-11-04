package client.REPLs;

import java.util.Scanner;

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
        return "help";
    }
}
