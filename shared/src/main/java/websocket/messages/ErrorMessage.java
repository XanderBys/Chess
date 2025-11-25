package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String msg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = msg;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public String getMessage() {
        return errorMessage;
    }
}
