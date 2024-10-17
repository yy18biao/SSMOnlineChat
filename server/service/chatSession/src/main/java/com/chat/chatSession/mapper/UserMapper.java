package com.chat.chatSession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.chatSession.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
