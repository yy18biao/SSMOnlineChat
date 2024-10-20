package com.chat.chatSession.rabbit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chat.chatSession.mapper.ChatSessionMapper;
import com.chat.chatSession.mapper.ChatSessionUserMapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.dto.RabbitChatSessionDto;
import com.chat.core.domain.dto.RabbitMessageDto;
import com.chat.core.enums.ResCode;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumeService {
    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private ChatSessionMapper chatSessionMapper;

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

    @RabbitListener(queues = "chatSession")
    public void consume(RabbitChatSessionDto rabbitChatSessionDto) {
        LoginUserData loginUserData = getLoginUserData(rabbitChatSessionDto.getToken());
        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());

        if (rabbitChatSessionDto.getDtoType().equals("updateLastMessageContent")) {
            // 修改会话的最新消息
            if(chatSessionMapper.updateChatSessionLastMessage(rabbitChatSessionDto.getChatSessionId(),
                    rabbitChatSessionDto.getLastMessageContent()) < 1){
                throw new ServiceException(ResCode.FRIEND_UPDATE_CHATSESSION);
            }
        }
    }
}
