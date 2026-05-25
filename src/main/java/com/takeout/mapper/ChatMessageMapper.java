package com.takeout.mapper;

import com.takeout.entity.ChatMessage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ChatMessageMapper {
    List<ChatMessage> findBySessionId(@Param("sessionId") String sessionId);
    int insert(ChatMessage message);
}
