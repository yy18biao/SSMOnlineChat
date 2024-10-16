package com.chat.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.friend.domain.Friend;
import com.chat.friend.domain.vo.FriendDataVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
    List<FriendDataVo> list(Long userId);
}
