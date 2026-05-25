package com.takeout.service.impl;

import com.takeout.entity.ChatMessage;
import com.takeout.entity.ChatSession;
import com.takeout.mapper.ChatMessageMapper;
import com.takeout.mapper.ChatSessionMapper;
import com.takeout.service.AiService;
import com.takeout.service.ChatService;
import com.takeout.websocket.BaolemeWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private AiService aiService;

    @Override
    public ChatSession getOrCreateSession(String userId) {
        ChatSession session = chatSessionMapper.findByUserId(userId);
        if (session == null) {
            session = new ChatSession();
            session.setId("session_" + userId);
            session.setUserId(userId);
            session.setUserEmail(userId + "@baoleme.com");
            session.setMode("bot");
            session.setStatus("active");
            session.setLastUpdated(Instant.now().toString());
            chatSessionMapper.insert(session);

            ChatMessage welcome = new ChatMessage();
            welcome.setId("msg_" + UUID.randomUUID().toString().substring(0, 8));
            welcome.setSessionId(session.getId());
            welcome.setRole("assistant");
            welcome.setContent("您好！我是饱了么智能AI客服小饱，有什么可以帮您的吗？");
            welcome.setTimestamp(Instant.now().toString());
            chatMessageMapper.insert(welcome);
        }
        return session;
    }

    @Override
    public List<ChatSession> getAllSessions() {
        return chatSessionMapper.findAll();
    }

    @Override
    public Map<String, Object> switchMode(String sessionId, String mode) {
        chatSessionMapper.updateMode(sessionId, mode);
        chatSessionMapper.updateLastUpdated(sessionId, Instant.now().toString());
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("mode", mode);
        return result;
    }

    @Override
    public Map<String, Object> resolveSession(String sessionId) {
        chatSessionMapper.updateStatus(sessionId, "resolved");
        chatSessionMapper.updateLastUpdated(sessionId, Instant.now().toString());
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @Override
    public List<ChatMessage> getMessages(String sessionId) {
        return chatMessageMapper.findBySessionId(sessionId);
    }

    @Override
    public Map<String, Object> sendMessage(String sessionId, String role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setId("msg_" + UUID.randomUUID().toString().substring(0, 8));
        msg.setSessionId(sessionId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setTimestamp(Instant.now().toString());
        chatMessageMapper.insert(msg);
        chatSessionMapper.updateLastUpdated(sessionId, Instant.now().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");

        if ("user".equals(role)) {
            ChatSession session = chatSessionMapper.findById(sessionId);
            if (session != null && "bot".equals(session.getMode())) {
                String reply = aiService.chatReply(content, sessionId);
                ChatMessage aiMsg = new ChatMessage();
                aiMsg.setId("msg_" + UUID.randomUUID().toString().substring(0, 8));
                aiMsg.setSessionId(sessionId);
                aiMsg.setRole("assistant");
                aiMsg.setContent(reply);
                aiMsg.setTimestamp(Instant.now().toString());
                chatMessageMapper.insert(aiMsg);
                result.put("message", toMessageMap(aiMsg));
            } else {
                result.put("message", toMessageMap(msg));
            }
        } else if ("merchant".equals(role)) {
            ChatSession session = chatSessionMapper.findById(sessionId);
            if (session != null) {
                BaolemeWebSocketServer.sendToUser("client", session.getUserId(),
                    "{\"type\":\"CHAT_MESSAGE\",\"payload\":{\"sessionId\":\""
                    + sessionId + "\",\"role\":\"merchant\",\"content\":\""
                    + content.replace("\"", "\\\"") + "\"}}");
            }
            result.put("message", toMessageMap(msg));
        }
        return result;
    }

    private Map<String, Object> toMessageMap(ChatMessage msg) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", msg.getId());
        map.put("role", msg.getRole());
        map.put("content", msg.getContent());
        map.put("timestamp", msg.getTimestamp());
        return map;
    }
}