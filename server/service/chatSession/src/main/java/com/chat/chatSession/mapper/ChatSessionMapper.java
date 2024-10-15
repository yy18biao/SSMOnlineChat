package com.chat.chatSession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.chatSession.domain.ChatSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

}
