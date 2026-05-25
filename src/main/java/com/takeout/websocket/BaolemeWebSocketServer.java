package com.takeout.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/baoleme/{role}/{userId}")
public class BaolemeWebSocketServer {

    private static final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("role") String role, @PathParam("userId") String userId) {
        String key = role + "_" + userId;
        clients.put(key, session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    @OnClose
    public void onClose(Session session, @PathParam("role") String role, @PathParam("userId") String userId) {
        String key = role + "_" + userId;
        clients.remove(key);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public static void sendToUser(String role, String userId, String jsonMsg) {
        String key = role + "_" + userId;
        Session target = clients.get(key);
        if (target != null && target.isOpen()) {
            try {
                target.getBasicRemote().sendText(jsonMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void broadcastToRole(String rolePrefix, String jsonMsg) {
        clients.forEach((key, session) -> {
            if (key.startsWith(rolePrefix) && session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(jsonMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
