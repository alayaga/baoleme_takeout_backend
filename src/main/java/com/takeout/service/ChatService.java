package com.takeout.service;

import com.takeout.entity.ChatMessage;
import com.takeout.entity.ChatSession;
import java.util.List;
import java.util.Map;

public interface ChatService {
    ChatSession getOrCreateSession(String userId);
    List<ChatSession> getAllSessions();
    Map<String, Object> switchMode(String sessionId, String mode);
    Map<String, Object> resolveSession(String sessionId);
    List<ChatMessage> getMessages(String sessionId);
    Map<String, Object> sendMessage(String sessionId, String role, String content);
}
