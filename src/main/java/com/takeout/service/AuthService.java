package com.takeout.service;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String username, String password);
    Map<String, Object> register(String username, String password, String role);
}
