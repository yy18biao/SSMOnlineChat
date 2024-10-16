package com.chat.chatSession.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.chat.chatSession.domain.ChatSession;
import com.chat.chatSession.domain.vo.ChatSessionVo;
import com.chat.chatSession.mapper.ChatSessionMapper;
import com.chat.chatSession.mapper.UserChatSessionMapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.security.service.TokenService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class ChatSessionService {
    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private UserChatSessionMapper userChatSessionMapper;

    @Resource
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    public List<ChatSessionVo> getChatSessionList(String token){
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if(loginUserData == null){
            return null;
        }

        // 获取用户所在的所有会话id
        List<ChatSessionVo> chatSessionVoList = userChatSessionMapper.list(loginUserData.getUserId());
        if(CollectionUtil.isEmpty(chatSessionVoList)){
            return null;
        }

        LinkedHashSet<Long> chatSessionIdList = new LinkedHashSet<>();
        for (ChatSessionVo chatSessionVo : chatSessionVoList) {
            chatSessionIdList.add(chatSessionVo.getChatSessionId());
        }
        chatSessionVoList.clear();

        System.out.println(chatSessionIdList);

        // 获取到所有会话的信息
        List<ChatSession> chatSessionList= chatSessionMapper.selectBatchIds(chatSessionIdList);
        if(CollectionUtil.isEmpty(chatSessionList)){
            return null;
        }

        for (ChatSession chatSession : chatSessionList) {
            ChatSessionVo chatSessionVo = BeanUtil.copyProperties(chatSession, ChatSessionVo.class);
            chatSessionVoList.add(chatSessionVo);
        }
        return chatSessionVoList;
    }
}
