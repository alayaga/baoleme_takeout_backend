package com.takeout.controller;

import com.takeout.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> result = authService.login(username, password);
        if (result == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "账号或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");
        Map<String, Object> result = authService.register(username, password, role);
        if (result == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "用户名已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
        return ResponseEntity.ok(result);
    }
}
