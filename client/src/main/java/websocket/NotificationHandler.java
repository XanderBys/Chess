package websocket;

import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_BOLD;

public class NotificationHandler {
    public void notify(ServerMessage serverMessage) {
        System.out.println(SET_TEXT_BOLD + serverMessage.toString());
    }
}