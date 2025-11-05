package client;

import client.REPLs.LoggedOutREPL;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class ChessClient {
    // TODO: remove hard-coding for the port in server URL
    private final String SERVER_URL = "http://localhost:8080";

    public void run() {
        // welcome message
        System.out.println("Welcome to the CS 240 chess program. Please login or register.");
        System.out.println(SET_TEXT_ITALIC + "Type 'help' for more information.");
        System.out.print(RESET_TEXT_ITALIC);

        Scanner scanner = new Scanner(System.in);
        ServerFacade sf = new ServerFacade(SERVER_URL);
        LoggedOutREPL loggedOutREPL = new LoggedOutREPL(scanner, sf);

        loggedOutREPL.run();
    }
}
