package com.chat.friend.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.enums.ResCode;
import com.chat.friend.domain.Friend;
import com.chat.friend.domain.User;
import com.chat.friend.domain.vo.FriendDataVo;
import com.chat.friend.domain.vo.FriendSearchVo;
import com.chat.friend.mapper.FriendMapper;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.friend.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {
    @Resource
    private FriendMapper friendMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    public List<FriendDataVo> list(String token){
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if(loginUserData == null){
            return null;
        }

        // 获取用户的所有好友信息
        List<Friend> friends = friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, loginUserData.getUserId()));

        List<FriendDataVo> friendDataVos = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(friends))
            friendDataVos = BeanUtil.copyToList(friends, FriendDataVo.class);
        return friendDataVos;
    }

    public FriendSearchVo search(Long friendId, String token){
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if(loginUserData == null){
            throw new ServiceException(ResCode.FAILED_UNAUTHORIZED);
        }

        return userMapper.search(friendId, loginUserData.getUserId());
    }

    public int updateRemark(Long friendId, String friendName, String token){
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if(loginUserData == null){
            throw new ServiceException(ResCode.FAILED_UNAUTHORIZED);
        }

        Friend friend = new Friend();
        friend.setFriendId(friendId);
        friend.setFriendName(friendName);
        friend.setUserId(loginUserData.getUserId());

        return friendMapper.update(friend, new LambdaUpdateWrapper<Friend>()
                .eq(Friend::getUserId, loginUserData.getUserId())
                .eq(Friend::getFriendId, friendId));
    }
}
