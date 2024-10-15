package com.chat.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.friend.domain.User;
import com.chat.friend.domain.vo.FriendSearchVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    FriendSearchVo search(Long friendId, Long userId);
}
