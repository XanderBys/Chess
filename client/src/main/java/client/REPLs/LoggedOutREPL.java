package client.REPLs;

import client.ClientAction;

import java.util.Scanner;

public class LoggedOutREPL extends REPL {
    public LoggedOutREPL(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        run("LOGGED OUT", ClientAction.QUIT);
    }

    @Override
    protected ClientAction evalInput(String input) {
        String cmd = getCmd(input);
        String[] params = getParams(input);

        return switch (cmd) {
            case "quit" -> ClientAction.QUIT;
            case "login" -> login(params);
            case "register" -> register(params);
            default -> help();
        };
    }

    private ClientAction login(String[] params) {
        // TODO: implement login
        return null;
    }

    private ClientAction register(String[] params) {
        // TODO: implement register
        return null;
    }

    private ClientAction help() {
        return ClientAction.NULL;
    }
}
