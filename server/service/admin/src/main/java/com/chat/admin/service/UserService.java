package com.chat.admin.service;

import com.chat.admin.domain.user.*;
import com.chat.admin.mapper.UserMapper;
import com.chat.core.enums.ResCode;
import com.chat.security.exception.ServiceException;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public List<UserVo> list(UserSearchDto userSearchDto){
        // 分页
        PageHelper.startPage(userSearchDto.getPageNum(), userSearchDto.getPageSize());
        return userMapper.list(userSearchDto);
    }

    public List<ApplyVo> applyList(UserSearchDto userSearchDto){
        // 分页
        PageHelper.startPage(userSearchDto.getPageNum(), userSearchDto.getPageSize());
        return userMapper.applyList(userSearchDto);
    }

    public int updateStatus(UserDto userDto){
        User user = userMapper.selectById(userDto.getUserId());
        if (user == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }
        user.setStatus(userDto.getStatus());
        return userMapper.updateById(user);
    }
}
