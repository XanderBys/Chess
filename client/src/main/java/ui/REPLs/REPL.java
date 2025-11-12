package ui.REPLs;

import server.ResponseException;
import server.ServerFacade;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

import static ui.EscapeSequences.*;

public abstract class REPL {
    protected Scanner scanner;
    protected ServerFacade serverFacade;

    /**
     * Starts the main loop
     *
     * @param state      the name of the loop (logged in, logged out, gameplay, etc.)
     * @param exitAction the action needed to leave the loop
     */
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

    /**
     * Evaluate a given input and execute the appropriate action based on that input
     * @param input a String provided by the user
     * @return the name of the action executed
     */
    protected abstract String evalInput(String input);

    /**
     * Prints the prompt for a user in the terminal
     * @param state the name of the loop (logged in, logged out, gameplay, etc.)
     */
    protected void printPrompt(String state) {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "[" + state + "]");
        System.out.print(SET_TEXT_COLOR_WHITE + " >>> ");
    }

    /**
     * Checks that the parameters are all non-empty strings
     * @param desiredLength the number of parameters desired
     * @param params a list of Strings representing the passed parameters
     * @return true if there is the correct number of parameters that are all nonempty strings
     */
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

    /**
     * Prints a help message to the console describing the available commands
     * @param commandDescriptions a HashMap from String to String[]. The keys represent the name of a command.
     *                            The 0th value in each String[] should be a description of the command and the
     *                            subsequent values should be the parameters for that command.
     * @return the String "help" if successful
     */
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

    /**
     * Executes a given action, handling errors appropriately.
     * @param actionName the name of the action (to be printed out for the user to see)
     * @param action A lambda expression containing the actual action to be run using an instance of ServerFacade
     * @param params the list of parameters for the ServerFacade action
     * @param numParams the number of parameters required by the action
     * @param successMessage displayed to the console if the action is successful. Can be an empty string.
     * @param errorMessages a HashMap mapping HTTP error codes to the message that should be displayed to the user
     * @return the result of 'action' if the action is successful, otherwise null
     */
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
            System.out.println(Objects.requireNonNullElseGet(message,
                    () -> "There was an error in processing your " + actionName + " request."));
        } catch (Exception e) {
            System.out.println("It's not possible to " + actionName + " right now. Please try again later.");
        }
        return null;
    }
}
