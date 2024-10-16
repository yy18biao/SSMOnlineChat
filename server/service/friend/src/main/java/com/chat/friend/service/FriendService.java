package com.chat.friend.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.enums.ResCode;
import com.chat.friend.domain.Friend;
import com.chat.friend.domain.FriendApply;
import com.chat.friend.domain.User;
import com.chat.friend.domain.vo.FriendDataVo;
import com.chat.friend.domain.vo.FriendSearchVo;
import com.chat.friend.domain.vo.SearchUserVo;
import com.chat.friend.mapper.FriendApplyMapper;
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
    private FriendApplyMapper friendApplyMapper;

    @Resource
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    private LoginUserData getLoginUserData(String token) {
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if (loginUserData == null) {
            throw new ServiceException(ResCode.FAILED_UNAUTHORIZED);
        }

        return loginUserData;
    }

    public List<FriendDataVo> list(String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        // 获取用户的所有好友信息
        return friendMapper.list(loginUserData.getUserId());
    }

    public FriendSearchVo search(Long friendId, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        return userMapper.search(friendId, loginUserData.getUserId());
    }

    public int updateRemark(Long friendId, String friendName, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        Friend friend = new Friend();
        friend.setFriendId(friendId);
        friend.setFriendName(friendName);
        friend.setUserId(loginUserData.getUserId());

        return friendMapper.update(friend, new LambdaUpdateWrapper<Friend>()
                .eq(Friend::getUserId, loginUserData.getUserId())
                .eq(Friend::getFriendId, friendId));
    }

    public boolean deleteFriend(Long friendId, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        return friendMapper.delete(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getFriendId, friendId)
                .eq(Friend::getUserId, loginUserData.getUserId())) > 0 &&
                friendMapper.delete(new LambdaQueryWrapper<Friend>()
                        .eq(Friend::getFriendId, loginUserData.getUserId())
                        .eq(Friend::getUserId, friendId)) > 0;
    }

    public SearchUserVo searchUser(String searchName) {
        return userMapper.searchUser(searchName);
    }

    public int addFriend(Long friendId, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        if (friendId.equals(loginUserData.getUserId())) {
            throw new ServiceException(ResCode.FRIEND_NO_ME);
        }

        // 判断两人是否已经是好友
        if (friendMapper.selectCount(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getFriendId, friendId)
                .eq(Friend::getUserId, loginUserData.getUserId())) > 0) {
            throw new ServiceException(ResCode.FRIEND_YES);
        }

        // 判断是否已经有过申请了
        if(friendApplyMapper.selectCount(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getUserId, loginUserData.getUserId())
                .eq(FriendApply::getFriendId, friendId)) > 0) {
            throw new ServiceException(ResCode.APPLYED);
        }

        // 判断对方是否向你发起了申请
        if(friendApplyMapper.selectCount(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getUserId, friendId)
                .eq(FriendApply::getFriendId, loginUserData.getUserId())) > 0) {
            throw new ServiceException(ResCode.FRIEND_APPLYED);
        }

        // 查询该用户的信息
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, friendId)) < 0) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        FriendApply apply = new FriendApply();
        apply.setUserId(loginUserData.getUserId());
        apply.setFriendId(friendId);
        apply.setNickname(loginUserData.getNickname());
        apply.setPhoto(loginUserData.getPhoto());

        return friendApplyMapper.insert(apply);
    }

    public List<FriendApply> friendApplyList(String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        List<FriendApply> friendApplies = friendApplyMapper.selectList(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getFriendId, loginUserData.getUserId()));
        System.out.println(friendApplies);

        return friendApplies;
    }

    public boolean agreeFriendApply(Long friendId, String token) {
        LoginUserData loginUserData = getLoginUserData(token);

        FriendApply friendApply = friendApplyMapper.selectOne(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getFriendId, loginUserData.getUserId()));

        Friend friend = new Friend();
        friend.setFriendId(friendId);
        friend.setUserId(loginUserData.getUserId());
        friend.setFriendName(friendApply.getNickname());
        friend.setFriendPhoto(friendApply.getPhoto());

        Friend friend1 = new Friend();
        friend1.setFriendId(loginUserData.getUserId());
        friend1.setUserId(friendId);
        friend1.setFriendName(loginUserData.getNickname());
        friend1.setFriendPhoto(loginUserData.getPhoto());

        return friendMapper.insert(friend) > 0
                && friendMapper.insert(friend1) > 0
                && friendApplyMapper.delete(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getFriendId, loginUserData.getUserId())) > 0;
    }

    public int refuseFriendApply(Long friendId, String token){
        LoginUserData loginUserData = getLoginUserData(token);

        return friendApplyMapper.delete(new LambdaQueryWrapper<FriendApply>()
                .eq(FriendApply::getFriendId, loginUserData.getUserId()));
    }
}
