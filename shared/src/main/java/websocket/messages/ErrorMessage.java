package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String msg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = msg;
    }
}
