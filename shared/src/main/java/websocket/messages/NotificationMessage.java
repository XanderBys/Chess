package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String msg) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = msg;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public String getMessage() {
        return message;
    }
}