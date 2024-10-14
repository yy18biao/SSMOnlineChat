package com.chat.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
