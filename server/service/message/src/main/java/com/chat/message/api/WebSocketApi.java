package com.chat.message.api;

import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.enums.ResCode;
import com.chat.message.domain.vo.MessageVo;
import com.chat.message.service.MessageService;
import com.chat.message.service.OnlineWebsocketService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

// websocket协议交互业务处理类
@Component
public class WebSocketApi extends TextWebSocketHandler {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    @Resource
    private MessageService messageService;

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

    // 收到消息回调
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){

    }

    // 连接建立回调
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        LoginUserData loginUserData = getLoginUserData(session.getId());
        onlineWebsocketService.online(loginUserData.getUserId(), session);
    }

    // 连接断开回调
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        LoginUserData loginUserData = getLoginUserData(session.getId());
        onlineWebsocketService.offline(loginUserData.getUserId(), session);
    }

    // 连接异常回调
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }
}
