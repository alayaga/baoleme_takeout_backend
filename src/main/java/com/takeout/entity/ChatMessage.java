package com.takeout.entity;

import lombok.Data;

@Data
public class ChatMessage {
    private String id;
    private String sessionId;
    private String role;
    private String content;
    private String timestamp;
}
