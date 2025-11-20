package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    /**
     * Adds a new connection to the manager
     *
     * @param gameID  which game the connection belongs to
     * @param session the WebSocket session to be added to the ConnectionManager
     */
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

    /**
     * Removes a session from the connection manager
     * @param gameID of the game the session belongs to
     * @param session the session to be removed
     */
    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    /**
     * Sends a message to all connections in a given game
     * @param gameID the ID of the game
     * @param excludeSession sessions to be excluded from the broadcast
     * @param message the message to be broadcasted
     * @throws IOException for WebSocket IO errors
     */
    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        HashSet<Session> set = connections.get(gameID);
        for (Session session : set) {
            if (!session.equals(excludeSession) && session.isOpen()) {
                session.getRemote().sendString(message.toString());
            }
        }
    }
}
