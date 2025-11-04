package client;

import client.REPLs.LoggedOutREPL;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class ChessClient {
    private final ClientState state = ClientState.LOGGED_OUT;

    public void run() {
        // welcome message
        System.out.println("Welcome to the CS 240 chess program. Please login or register.");
        System.out.println(SET_TEXT_ITALIC + "Type 'help' for more information.");
        System.out.print(RESET_TEXT_ITALIC);

        Scanner scanner = new Scanner(System.in);
        LoggedOutREPL loggedOutREPL = new LoggedOutREPL(scanner);

        loggedOutREPL.run();
    }
}
