package com.takeout.entity;

import lombok.Data;

@Data
public class ChatSession {
    private String id;
    private String userId;
    private String userEmail;
    private String mode;
    private String status;
    private String lastUpdated;
}
