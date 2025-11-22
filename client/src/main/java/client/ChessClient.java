package client;

import server.ServerFacade;
import ui.repls.LoggedOutREPL;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class ChessClient {
    private final static int DEFAULT_PORT = 8080;
    public final static String SERVER_URL = "http://localhost:" + DEFAULT_PORT;

    public void run(int port) {
        // welcome message
        System.out.println("Welcome to the CS 240 chess program. Please login or register.");
        System.out.println(SET_TEXT_ITALIC + "Type 'help' for more information.");
        System.out.print(RESET_TEXT_ITALIC);

        Scanner scanner = new Scanner(System.in);
        ServerFacade sf = new ServerFacade(SERVER_URL);
        LoggedOutREPL loggedOutREPL = new LoggedOutREPL(scanner, sf);

        loggedOutREPL.run();
    }

    public void run() {
        run(DEFAULT_PORT);
    }
}
