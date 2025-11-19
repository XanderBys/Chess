package websocket;

import org.eclipse.jetty.websocket.api.Session;

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
}
