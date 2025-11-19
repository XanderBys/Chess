package websocket.commands;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {
    private final CommandType commandType;
    private final String authToken;
    private final int gameID;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return commandType() == that.commandType() &&
                Objects.equals(authToken(), that.authToken()) &&
                Objects.equals(gameID(), that.gameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType(), authToken(), gameID());
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType commandType() {
        return commandType;
    }

    public String authToken() {
        return authToken;
    }

    public int gameID() {
        return gameID;
    }
}
