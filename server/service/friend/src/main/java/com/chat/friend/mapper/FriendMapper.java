package com.chat.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.friend.domain.Friend;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
}
