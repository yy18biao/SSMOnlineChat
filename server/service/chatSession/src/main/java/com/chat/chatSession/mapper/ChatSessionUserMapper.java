package com.chat.chatSession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.chatSession.domain.ChatSessionUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatSessionUserMapper extends BaseMapper<ChatSessionUser> {
    @Select("SELECT chatSessionId FROM chatSessionUser where userId = #{userId}")
    List<Long> selectAll(Long userId);
}
