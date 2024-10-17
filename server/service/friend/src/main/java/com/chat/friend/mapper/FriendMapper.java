package com.chat.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.friend.domain.Friend;
import com.chat.friend.domain.vo.FriendDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
    List<FriendDataVo> list(Long userId);
    @Update("update friend set friendName=#{friendName} where friendId=#{friendId} and userId=#{userId}")
    int updateRemark(String friendName, Long friendId, Long userId);
}
