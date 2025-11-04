package client.REPLs;

import client.ClientAction;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public abstract class REPL {
    protected Scanner scanner;

    public void run(String state, ClientAction exitAction) {
        ClientAction result = ClientAction.NULL;

        do {
            printPrompt(state);
            String input = scanner.nextLine();

            try {
                result = evalInput(input);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }

        } while (!result.equals(exitAction));
    }

    protected abstract ClientAction evalInput(String input);

    protected void printPrompt(String state) {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "[" + state + "]");
        System.out.print(SET_TEXT_COLOR_WHITE + " >>> ");
    }

    protected String getCmd(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if (tokens.length > 0) {
            return tokens[0];
        } else {
            return "help";
        }
    }

    protected String[] getParams(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if (tokens.length > 1) {
            return Arrays.copyOfRange(tokens, 1, tokens.length);
        } else {
            return new String[0];
        }
    }
}
