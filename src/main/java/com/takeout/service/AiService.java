package com.takeout.service;

import java.util.List;
import java.util.Map;

public interface AiService {
    Map<String, Object> recommend(List<String> tags, String favoriteCategory, String customCraving);
    String chatReply(String userMessage, String sessionId);
}
