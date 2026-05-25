package com.takeout.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.entity.UserAccount;
import com.takeout.entity.UserProfile;
import com.takeout.mapper.UserAccountMapper;
import com.takeout.mapper.UserProfileMapper;
import com.takeout.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> login(String username, String password) {
        UserAccount account = userAccountMapper.findByUsername(username);
        if (account == null || !account.getPassword().equals(password)) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        String token = UUID.randomUUID().toString().replace("-", "");
        result.put("token", token);
        result.put("role", account.getRole());

        UserProfile profile = userProfileMapper.findById(account.getProfileId());
        if (profile != null) {
            Map<String, Object> profileMap = new HashMap<>();
            profileMap.put("id", profile.getId());
            profileMap.put("name", profile.getName());
            profileMap.put("historyOrdersCount", profile.getHistoryOrdersCount());
            profileMap.put("favoriteCategory", profile.getFavoriteCategory());
            try {
                List<String> tags = objectMapper.readValue(
                    profile.getTags(), new TypeReference<List<String>>() {});
                profileMap.put("tags", tags);
            } catch (Exception e) {
                profileMap.put("tags", new ArrayList<>());
            }
            result.put("profile", profileMap);
        }
        return result;
    }

    @Override
    public Map<String, Object> register(String username, String password, String role) {
        UserAccount existing = userAccountMapper.findByUsername(username);
        if (existing != null) {
            return null;
        }
        String profileId = "u_" + UUID.randomUUID().toString().substring(0, 6);
        UserProfile profile = new UserProfile();
        profile.setId(profileId);
        profile.setName(username);
        profile.setTags("[]");
        profile.setHistoryOrdersCount(0);
        profile.setFavoriteCategory("热销推荐");
        userProfileMapper.insert(profile);

        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(role != null ? role : "client");
        account.setProfileId(profileId);
        userAccountMapper.insert(account);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "注册成功");
        return result;
    }
}