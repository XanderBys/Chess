package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String msg) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = msg;
    }
}