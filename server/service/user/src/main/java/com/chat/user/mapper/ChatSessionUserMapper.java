package com.chat.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.user.domain.ChatSessionUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatSessionUserMapper extends BaseMapper<ChatSessionUser> {
    @Select("SELECT userId FROM chatSessionUser where chatSessionId = #{chatSessionId}")
    List<Long> selectAllUserId(Long chatSessionId);
}
