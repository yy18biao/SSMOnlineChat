package com.chat.chatSession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.chatSession.domain.UserChatSession;
import com.chat.chatSession.domain.vo.ChatSessionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserChatSessionMapper extends BaseMapper<UserChatSession> {
    @Select("SELECT chatSessionId FROM userChatSession where userId = #{userId} ORDER BY updateTime DESC")
    List<ChatSessionVo> list(Long userId);
}
