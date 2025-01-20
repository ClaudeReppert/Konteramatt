package lu.Kontermatt;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/cardUpdates")
public class KontermattWebSocket {
    // Store all active sessions
    private static Set<Session> clients = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("New client connected, session ID: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // For now, simply log received messages (optional)
        System.out.println("Received message from client: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("Client disconnected, session ID: " + session.getId());
    }

    // Method to broadcast card updates to all clients
    public static void broadcastCardUpdate(String cardUpdateJson) {
        for (Session client : clients) {
            if (client.isOpen()) {
                try {
                    client.getBasicRemote().sendText(cardUpdateJson);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}