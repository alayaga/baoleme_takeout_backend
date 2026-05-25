package com.takeout.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.entity.Dish;
import com.takeout.mapper.DishMapper;
import com.takeout.service.AiService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    @Autowired
    private DishMapper dishMapper;

    @Value("${ai.api.key:}")
    private String apiKey;
    @Value("${ai.api.url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String apiUrl;
    @Value("${ai.api.model:qwen-turbo}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    public Map<String, Object> recommend(List<String> tags, String favoriteCategory, String customCraving) {
        List<Dish> dishes = dishMapper.findAll();
        List<Dish> available = new ArrayList<>();
        for (Dish d : dishes) {
            if ("active".equals(d.getStatus()) && d.getStock() > 0) {
                available.add(d);
            }
        }

        StringBuilder dishList = new StringBuilder();
        for (Dish d : available) {
            dishList.append(d.getId()).append(" - ").append(d.getName())
                    .append(" (").append(d.getPrice()).append("元, ")
                    .append(d.getCategory()).append(")\n");
        }

        String prompt = "你是饱了么外卖平台的智能营养顾问。现有库存菜品如下：\n" + dishList
                + "\n用户标签：" + tags + "\n偏好分类：" + favoriteCategory
                + "\n用户需求：" + (customCraving != null ? customCraving : "随便推荐")
                + "\n\n请推荐1-3个最合适的菜品，必须返回严格的JSON格式："
                + "{\"recommendationTitle\":\"...\",\"reason\":\"...\","
                + "\"items\":[{\"dishId\":\"...\",\"dishName\":\"...\",\"specialReason\":\"...\"}]}";

        String aiResponse = callAi(prompt);
        try {
            return objectMapper.readValue(aiResponse, Map.class);
        } catch (Exception e) {
            log.warn("AI返回解析失败，使用兜底推荐", e);
            return buildFallbackRecommendation(available);
        }
    }

    @Override
    public String chatReply(String userMessage, String sessionId) {
        List<Dish> dishes = dishMapper.findAll();
        StringBuilder menu = new StringBuilder();
        for (Dish d : dishes) {
            if ("active".equals(d.getStatus()) && d.getStock() > 0) {
                menu.append("- ").append(d.getName())
                    .append(" | ").append(d.getPrice()).append("元")
                    .append(" | 分类:").append(d.getCategory())
                    .append(" | 月售:").append(d.getSales())
                    .append(" | 简介:").append(d.getDescription())
                    .append("\n");
            }
        }

        String prompt = "【重要规则】你只能基于以下菜单信息回答，严禁编造菜单中不存在的菜品。\n\n"
                + "当前饱了么平台完整菜单：\n" + menu
                + "\n顾客问：" + userMessage
                + "\n\n请根据以上菜单信息，用简短友好的语气回复。如果顾客问的菜品不在菜单中，请明确告知没有并推荐菜单中相近的菜品。";

        String reply = callAi(prompt);
        if (reply == null || reply.isEmpty()) {
            return "抱歉，我暂时无法回复，请稍后再试或转接人工客服。";
        }
        return reply;
    }

    private String callAi(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("AI API Key 未配置，返回空");
            return null;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("temperature", 0.7);
            body.put("max_tokens", 500);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", "你是饱了么外卖平台的AI助手小饱，回复简洁友好。");
            messages.add(sysMsg);
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);
            body.put("messages", messages);

            String json = objectMapper.writeValueAsString(body);
            String url = apiUrl.endsWith("/")
                    ? apiUrl + "chat/completions"
                    : apiUrl + "/chat/completions";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            log.info("调用AI接口: {}", url);
            try (Response response = httpClient.newCall(request).execute()) {
                String respBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    JsonNode root = objectMapper.readTree(respBody);
                    return root.at("/choices/0/message/content").asText();
                } else {
                    log.error("AI接口返回错误 status={}, body={}", response.code(), respBody);
                }
            }
        } catch (Exception e) {
            log.error("调用AI接口异常", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildFallbackRecommendation(List<Dish> dishes) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recommendationTitle", "今日精选推荐");
        result.put("reason", "根据热销排行为您推荐");
        List<Map<String, Object>> items = new ArrayList<>();
        int count = Math.min(3, dishes.size());
        for (int i = 0; i < count; i++) {
            Dish d = dishes.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("dishId", d.getId());
            item.put("dishName", d.getName());
            item.put("specialReason", d.getDescription());
            items.add(item);
        }
        result.put("items", items);
        return result;
    }
}