package ui.REPLs;

import server.ResponseException;
import server.ServerFacade;

import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;

import static ui.EscapeSequences.*;

public abstract class REPL {
    protected Scanner scanner;
    protected ServerFacade serverFacade;

    public void run(String state, String exitAction) {
        String result = "";

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

    protected abstract String evalInput(String input);

    protected void printPrompt(String state) {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "[" + state + "]");
        System.out.print(SET_TEXT_COLOR_WHITE + " >>> ");
    }

    protected boolean verifyParameters(int desiredLength, String[] params) {
        if (params.length == desiredLength) {
            for (String param : params) {
                if (param.isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    protected String help(HashMap<String, String[]> commandDescriptions) {
        // iterate through commandDescriptions and print each one
        for (String key : commandDescriptions.keySet()) {
            String[] params = commandDescriptions.get(key);
            System.out.print(SET_TEXT_COLOR_MAGENTA + key + " ");
            for (int i = 1; i < params.length; i++) {
                System.out.print("<" + params[i].toUpperCase() + "> ");
            }

            System.out.println("- " + SET_TEXT_COLOR_WHITE + params[0]);
        }
        return "help";
    }

    protected Object executeServerFacadeAction(String actionName, Function<String[], Object> action, String[] params,
                                               int numParams,
                                               String successMessage,
                                               HashMap<Integer, String> errorMessages) {
        try {
            if (verifyParameters(numParams, params)) {
                Object result = action.apply(params);
                if (!successMessage.isEmpty()) {
                    System.out.println(successMessage);
                }
                return result;
            } else {
                System.out.println(actionName + " requires exactly " + numParams + " parameters. Please try again.");
            }
        } catch (ResponseException e) {
            if (e.getErrorCode() == 500) {
                System.out.println(e.getMessage());
                return null;
            }

            String message = errorMessages.get(e.getErrorCode());
            if (message != null) {
                System.out.println(message);
            } else {
                System.out.println("There was an error in processing your " + actionName + " request.");
            }
        } catch (Exception e) {
            System.out.println("It's not possible to " + actionName + " right now. Please try again later.");
        }
        return null;
    }
}
