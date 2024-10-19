package com.chat.message.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.enums.ResCode;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.message.domain.Message;
import com.chat.message.domain.dto.MessageDto;
import com.chat.message.domain.vo.MessageVo;
import com.chat.message.mapper.ChatSessionMapper;
import com.chat.message.mapper.ChatSessionUserMapper;
import com.chat.message.mapper.MessageMapper;
import com.chat.message.service.OnlineWebsocketService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// websocket协议交互业务处理类
@Component
public class WebSocketApi extends TextWebSocketHandler {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private ChatSessionMapper chatSessionMapper;

    @Resource
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // 收到消息回调
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        MessageDto messageDto = new MessageDto();
        // 反序列化收到的消息请求
        messageDto = objectMapper.readValue(message.getPayload(), MessageDto.class);

        // 判断消息类型
        if(messageDto.getMessageType().equals(3)){
            // 获取当前用户信息
            LoginUserData loginUserData = getLoginUserData(messageDto.getToken());
            // 用户上线
            onlineWebsocketService.online(loginUserData.getUserId(), session);
        } else if (messageDto.getMessageType().equals(1)) {
            // 获取当前用户信息
            LoginUserData loginUserData = getLoginUserData(messageDto.getToken());
            ThreadLocalUtil.set("curUserId", loginUserData.getUserId());
            messageDto.setMessageNickname(loginUserData.getNickname());

            // 将消息存到数据库
            Message newMessage = BeanUtil.copyProperties(messageDto, Message.class);
            if(messageMapper.insert(newMessage) < 1) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(null)));
                ThreadLocalUtil.remove();
                return;
            }

            // 获取当前会话中的所有用户
            List<Long> userIds = chatSessionUserMapper.selectAllUserId(messageDto.getChatSessionId());
            if(userIds == null || userIds.isEmpty()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(null)));
                ThreadLocalUtil.remove();
                return;
            }

            // 修改当前会话的最后一条消息数据
            if (chatSessionMapper.updateChatSessionLastMessageInt(messageDto.getChatSessionId(), messageDto.getContent()) < 1) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(null)));
                ThreadLocalUtil.remove();
                return;
            }

            // 将消息转发给当前会话中所有已上线的用户(包括自己)
            MessageVo messageVo = new MessageVo();
            messageVo.setNickname(loginUserData.getNickname());
            messageVo.setChatSessionId(messageDto.getChatSessionId());
            messageVo.setContent(messageDto.getContent());
            messageVo.setPhoto(loginUserData.getPhoto());
            messageVo.setUserId(loginUserData.getUserId());
            messageVo.setMessageType(1);
            messageVo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            for (Long userId : userIds) {
                WebSocketSession socketSession = onlineWebsocketService.getSession(userId);
                if (socketSession != null) {
                    socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageVo)));
                }
            }

            ThreadLocalUtil.remove();
        }
    }

    // 连接建立回调
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    // 连接断开回调
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    // 连接异常回调
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }
}
