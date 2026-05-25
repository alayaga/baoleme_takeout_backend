package com.takeout.mapper;

import com.takeout.entity.ChatSession;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ChatSessionMapper {
    ChatSession findById(@Param("id") String id);
    ChatSession findByUserId(@Param("userId") String userId);
    List<ChatSession> findAll();
    int insert(ChatSession session);
    int updateMode(@Param("id") String id, @Param("mode") String mode);
    int updateStatus(@Param("id") String id, @Param("status") String status);
    int updateLastUpdated(@Param("id") String id, @Param("lastUpdated") String lastUpdated);
}
