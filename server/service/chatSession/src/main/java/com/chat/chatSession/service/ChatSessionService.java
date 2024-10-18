package com.chat.chatSession.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.chatSession.domain.ChatSession;
import com.chat.chatSession.domain.ChatSessionUser;
import com.chat.chatSession.domain.User;
import com.chat.chatSession.domain.vo.ChatSessionVo;
import com.chat.chatSession.mapper.ChatSessionMapper;
import com.chat.chatSession.mapper.ChatSessionUserMapper;
import com.chat.chatSession.mapper.UserMapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.enums.ResCode;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class ChatSessionService {
    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private UserMapper userMapper;

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

    public List<ChatSessionVo> getChatSessionList(String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        // 获取用户所在的所有会话id
        LinkedHashSet<Long> chatSessionIds = new LinkedHashSet<>(chatSessionUserMapper.selectAll(loginUserData.getUserId()));
        if (CollectionUtil.isEmpty(chatSessionIds)) {
            return null;
        }
        System.out.println(chatSessionIds);

        // 获取到所有会话的信息
        List<ChatSession> chatSessionList = chatSessionMapper.selectByChatSessionIds(chatSessionIds);
        if (CollectionUtil.isEmpty(chatSessionList)) {
            return null;
        }
        System.out.println(chatSessionList);

        // 获取每个单聊会话的好友头像和昵称
        for (ChatSession chatSession : chatSessionList) {
            if (chatSession.getChatSessionType() == 1) {
                // 获取好友的id
                List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(new LambdaQueryWrapper<ChatSessionUser>()
                        .eq(ChatSessionUser::getChatSessionId, chatSession.getChatSessionId()));

                // 获取好友的头像
                if (chatSessionUserList != null) {
                    for (ChatSessionUser chatSessionUser : chatSessionUserList) {
                        if (chatSessionUser.getUserId().equals(loginUserData.getUserId()))
                            continue;
                        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                                .select(User::getPhoto, User::getNickname)
                                .eq(User::getUserId, chatSessionUser.getUserId()));
                        if (user != null) {
                            chatSession.setChatSessionPhoto(user.getPhoto());
                            chatSession.setChatSessionName(user.getNickname());
                        }
                    }
                }
            }
        }

        return BeanUtil.copyToList(chatSessionList, ChatSessionVo.class);
    }

    public String addChatSession(Long friendId, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        // 获取当前用户所在的所有会话id
        List<Long> chatSessionIds = chatSessionUserMapper.selectAll(loginUserData.getUserId());
        // 获取好友所在的所有会话
        List<Long> chatSessionIds2 = chatSessionUserMapper.selectAll(friendId);
        if (CollectionUtil.isNotEmpty(chatSessionIds) && CollectionUtil.isNotEmpty(chatSessionIds2)) {
            for (Long chatSessionId : chatSessionIds) {
                if (chatSessionIds2.contains(chatSessionId)) {
                    return chatSessionId.toString();
                }
            }
        }

        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());

        // 新建一个会话
        ChatSession chatSession = new ChatSession();
        chatSessionMapper.insert(chatSession);
        Long newChatSessionId = chatSession.getChatSessionId();

        // 插入关联表
        chatSessionUserMapper.insert(new ChatSessionUser(loginUserData.getUserId(), newChatSessionId));
        chatSessionUserMapper.insert(new ChatSessionUser(friendId, newChatSessionId));

        ThreadLocalUtil.remove();

        return newChatSessionId.toString();
    }

    public ChatSessionVo searchChatSession(Long chatSessionId, String token) {
        LoginUserData loginUserData = getLoginUserData(token);

        // 获取会话信息
        ChatSession chatSession = chatSessionMapper.selectOne(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getChatSessionId, chatSessionId));

        // 如果会话头像为空并且为单聊则获取对方的头像
        if (chatSession.getChatSessionType() == 1 && chatSession.getChatSessionPhoto() == null) {
            // 获取好友的id
            List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(new LambdaQueryWrapper<ChatSessionUser>()
                    .eq(ChatSessionUser::getChatSessionId, chatSession.getChatSessionId()));

            // 获取好友的头像
            if (chatSessionUserList != null) {
                for (ChatSessionUser chatSessionUser : chatSessionUserList) {
                    if (chatSessionUser.getUserId().equals(loginUserData.getUserId()))
                        continue;
                    User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                            .select(User::getPhoto, User::getNickname)
                            .eq(User::getUserId, chatSessionUser.getUserId()));
                    if (user != null) {
                        chatSession.setChatSessionPhoto(user.getPhoto());
                        chatSession.setChatSessionName(user.getNickname());
                    }
                }
            }
        }

        return BeanUtil.copyProperties(chatSession, ChatSessionVo.class);
    }

    public List<Long> selectAllUserId(Long chatSessionId){
        return chatSessionUserMapper.selectAllUserId(chatSessionId);
    }
}
