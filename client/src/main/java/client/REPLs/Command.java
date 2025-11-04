package client.REPLs;

import java.util.Arrays;
import java.util.Objects;

public class Command {
    private final String name;
    private final String[] params;

    public Command(String text) {
        String[] tokens = text.toLowerCase().split(" ");

        if (tokens.length > 0) {
            name = tokens[0];
            params = Arrays.copyOfRange(tokens, 1, tokens.length);
        } else {
            name = "help";
            params = new String[0];
        }

    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Command command = (Command) o;
        return Objects.equals(getName(), command.getName()) && Objects.deepEquals(getParams(), command.getParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), Arrays.hashCode(getParams()));
    }
}
