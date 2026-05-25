package com.takeout.controller;

import com.takeout.entity.ChatMessage;
import com.takeout.entity.ChatSession;
import com.takeout.service.AiService;
import com.takeout.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private AiService aiService;

    @GetMapping("/chat/session")
    public ChatSession getSession(@RequestParam String userId) {
        return chatService.getOrCreateSession(userId);
    }

    @GetMapping("/chat/sessions/all")
    public List<ChatSession> getAllSessions() {
        return chatService.getAllSessions();
    }

    @PostMapping("/chat/session/mode")
    public Map<String, Object> switchMode(@RequestBody Map<String, String> body) {
        return chatService.switchMode(body.get("sessionId"), body.get("mode"));
    }

    @PutMapping("/chat/session/{sessionId}/resolve")
    public Map<String, Object> resolve(@PathVariable String sessionId) {
        return chatService.resolveSession(sessionId);
    }

    @GetMapping("/chat/messages/{sessionId}")
    public List<ChatMessage> getMessages(@PathVariable String sessionId) {
        return chatService.getMessages(sessionId);
    }

    @PostMapping("/chat/messages")
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> body) {
        return chatService.sendMessage(body.get("sessionId"), body.get("role"), body.get("content"));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/gemini/recommend")
    public Map<String, Object> recommend(@RequestBody Map<String, Object> body) {
        List<String> tags = (List<String>) body.get("tags");
        String favoriteCategory = (String) body.get("favoriteCategory");
        String customCraving = (String) body.get("customCraving");
        return aiService.recommend(tags, favoriteCategory, customCraving);
    }
}