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

// -------------------------------------------------------------

    // 客服请求体
    // {
    //     "model": "qwen-turbo",
    //     "temperature": 0.7,
    //     "max_tokens": 500,
    //     "messages": [
    //       {
    //         "role": "system",
    //         "content": "你是饱了么外卖平台的AI助手小饱，回复简洁友好。"
    //       },
    //       {
    //         "role": "user",
    //         "content": "【重要规则】你只能基于以下菜单信息回答，严禁编造菜单中不存在的菜品。\n\n当前饱了么平台完整菜单：\n- 招牌黄焖鸡米饭 | 22.00元 | 分类:热销推荐 | 月售:1250 | 简介:精选嫩滑鸡腿肉，搭配香菇、青椒与秘制酱汁，微火慢炖，汤浓肉香。\n- 川味麻辣香锅 | 38.00元 | 分类:川湘名菜 | 月售:890 | 简介:多种新鲜蔬菜与肉类自由搭配，大火爆炒，麻辣鲜香，一锅满足。\n- 经典牛肉拉面 | 18.00元 | 分类:快餐简餐 | 月售:2100 | 简介:手工拉制面条，搭配慢炖牛肉与浓郁骨汤，撒上香菜与葱花。\n\n顾客问：有没有辣一点又下饭的？\n\n请根据以上菜单信息，用简短友好的语气回复。如果顾客问的菜品不在菜单中，请明确告知没有并推荐菜单中相近的菜品。"
    //       }
    //     ]
    //   }
    @PostMapping("/chat/messages")
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> body) {
        return chatService.sendMessage(body.get("sessionId"), body.get("role"), body.get("content"));
    }

    // 客服响应体
    // {
    //     "choices": [
    //       {
    //         "message": {
    //           "role": "assistant",
    //           "content": "有的，推荐您试试川味麻辣香锅，麻辣鲜香、配菜丰富，比较适合想吃辣和下饭的需求。如果想要更稳妥一点，也可以选择招牌黄焖鸡米饭。"
    //         },
    //         "index": 0,
    //         "finish_reason": "stop"
    //       }
    //     ],
    //     "usage": {
    //       "prompt_tokens": 310,
    //       "completion_tokens": 70,
    //       "total_tokens": 380
    //     },
    //     "request_id": "demo-request-id"
    //   }

// -------------------------------------------------------------

    // 推荐菜品请求体
    // {
    //     "model": "qwen-turbo",
    //     "temperature": 0.7,
    //     "max_tokens": 500,
    //     "messages": [
    //       {
    //         "role": "system",
    //         "content": "你是饱了么外卖平台的AI助手小饱，回复简洁友好。"
    //       },
    //       {
    //         "role": "user",
    //         "content": "你是饱了么外卖平台的智能营养顾问。现有库存菜品如下：\nd1 - 招牌黄焖鸡米饭 (22.00元, 热销推荐)\nd2 - 川味麻辣香锅 (38.00元, 川湘名菜)\nd3 - 经典牛肉拉面 (18.00元, 快餐简餐)\n\n用户标签：[熬夜加班, 偏爱重口味, 快捷简餐]\n偏好分类：川湘名菜\n用户需求：想吃下饭一点的\n\n请推荐1-3个最合适的菜品，必须返回严格的JSON格式：{\"recommendationTitle\":\"...\",\"reason\":\"...\",\"items\":[{\"dishId\":\"...\",\"dishName\":\"...\",\"specialReason\":\"...\"}]}"
    //       }
    //     ]
    //   }

    @SuppressWarnings("unchecked")
    @PostMapping("/gemini/recommend")
    public Map<String, Object> recommend(@RequestBody Map<String, Object> body) {
        List<String> tags = (List<String>) body.get("tags");
        String favoriteCategory = (String) body.get("favoriteCategory");
        String customCraving = (String) body.get("customCraving");
        return aiService.recommend(tags, favoriteCategory, customCraving);
    }

    // 推荐菜品响应体
    // {
    //     "choices": [
    //       {
    //         "message": {
    //           "role": "assistant",
    //           "content": "{\"recommendationTitle\":\"下饭重口味推荐\",\"reason\":\"根据您的川湘偏好和加班场景，推荐口味浓郁、饱腹感强的菜品。\",\"items\":[{\"dishId\":\"d2\",\"dishName\":\"川味麻辣香锅\",\"specialReason\":\"麻辣鲜香，适合偏爱重口味的用户。\"},{\"dishId\":\"d1\",\"dishName\":\"招牌黄焖鸡米饭\",\"specialReason\":\"有饭有肉，适合加班时快速补充能量。\"}]}"
    //         },
    //         "index": 0,
    //         "finish_reason": "stop"
    //       }
    //     ],
    //     "usage": {
    //       "prompt_tokens": 260,
    //       "completion_tokens": 120,
    //       "total_tokens": 380
    //     },
    //     "request_id": "demo-request-id"
    //   }
    // 取choices[0].message.content，然后把里面的 JSON 字符串解析成对象返回给前端。
}