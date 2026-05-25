package com.takeout.entity;

import lombok.Data;

@Data
public class UserAccount {
    private String username;
    private String password;
    private String role;
    private String profileId;
}
