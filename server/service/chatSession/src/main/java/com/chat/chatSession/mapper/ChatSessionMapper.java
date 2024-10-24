package com.chat.chatSession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.chatSession.domain.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.LinkedHashSet;
import java.util.List;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
    List<ChatSession> selectByChatSessionIds(@Param("chatSessionIds") LinkedHashSet<Long> chatSessionIds);

    @Update("update chatSession set chatSessionLastMessage = #{chatSessionLastMessage} where chatSessionId = #{chatSessionId}")
    int updateChatSessionLastMessage(Long chatSessionId, String chatSessionLastMessage);
}
