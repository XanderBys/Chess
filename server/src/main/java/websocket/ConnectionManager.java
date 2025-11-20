package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void put(int gameID, Session session) {
        HashSet<Session> gameConnections = connections.get(gameID);

        if (gameConnections == null) {
            gameConnections = new HashSet<>();
            gameConnections.add(session);

            connections.put(gameID, gameConnections);
        } else {
            connections.get(gameID).add(session);
        }
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        HashSet<Session> set = connections.get(gameID);
        for (Session session : set) {
            if (!session.equals(excludeSession) && session.isOpen()) {
                session.getRemote().sendString(message.toString());
            }
        }
    }
}
