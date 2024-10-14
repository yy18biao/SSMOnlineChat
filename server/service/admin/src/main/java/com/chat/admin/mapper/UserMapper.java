package com.chat.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.admin.domain.user.ApplyVo;
import com.chat.admin.domain.user.User;
import com.chat.admin.domain.user.UserSearchDto;
import com.chat.admin.domain.user.UserVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<UserVo> list(UserSearchDto userSearchDto);

    List<ApplyVo> applyList(UserSearchDto userSearchDto);
}
