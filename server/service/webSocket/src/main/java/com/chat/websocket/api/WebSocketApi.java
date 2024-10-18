package com.chat.websocket.api;

import com.chat.websocket.domain.Resp;
import com.chat.websocket.domain.WebSocketReqDto;
import com.chat.websocket.domain.vo.UserVo;
import com.chat.websocket.service.OnlineWebsocketService;
import com.chat.websocket.domain.dto.UpdateMessageDto;
import com.chat.websocket.domain.vo.MessageVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// websocket协议交互业务处理类
@Component
public class WebSocketApi extends TextWebSocketHandler {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    @Resource
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 收到消息回调
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        WebSocketReqDto webSocketReqDto = new WebSocketReqDto();
        // 反序列化收到的消息请求
        webSocketReqDto = objectMapper.readValue(message.getPayload(), WebSocketReqDto.class);

        // 判断消息类型
        if (webSocketReqDto.getMessageType().equals(1)) {
            // 文本消息类型

            // 获取当前用户信息
            UserVo userVo = (UserVo) Objects.requireNonNull(restTemplate.postForObject("http://127.0.0.1:9999/user/getUser",
                    webSocketReqDto.getToken(), Resp.class)).getData();
            System.out.println(userVo);

            // 将消息存到数据库
            if (Objects.requireNonNull(restTemplate.postForObject("http://127.0.0.1:9999/message/addMessage",
                    webSocketReqDto, Resp.class)).getCode() != 200) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(null)));
            }

            // 获取当前会话中的所有用户
            List<Long> userIds = restTemplate.postForObject("http://127.0.0.1:9999/chatSession/selectAllUserId",
                    webSocketReqDto.getChatSessionId(), List.class);
            if(userIds == null || userIds.isEmpty()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(null)));
            }
            System.out.println(userIds);

            // 修改当前会话的最后一条消息数据
            UpdateMessageDto updateMessageDto = new UpdateMessageDto();
            updateMessageDto.setChatSessionId(webSocketReqDto.getChatSessionId());
            updateMessageDto.setMessage(webSocketReqDto.getContent());
            if (Objects.requireNonNull(restTemplate.postForObject("http://127.0.0.1:9999/chatSession/updateChatSessionLastMessage",
                    updateMessageDto, Resp.class)).getCode() != 200) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(null)));
            }

            // 将消息转发给当前会话中所有已上线的用户(包括自己)
            MessageVo messageVo = new MessageVo();
            messageVo.setNickname(userVo.getNickname());
            messageVo.setContent(webSocketReqDto.getContent());
            messageVo.setPhoto(userVo.getPhoto());
            messageVo.setUserId(userVo.getUserId());
            messageVo.setCreateTime(LocalDateTime.now());
            for (Long userId : userIds) {
                WebSocketSession socketSession = onlineWebsocketService.getSession(userId);
                if (socketSession != null) {
                    socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageVo)));
                }
            }
        }
    }

    // 连接建立回调
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        onlineWebsocketService.online((Long)session.getAttributes().get("userId"), session);
    }

    // 连接断开回调
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        onlineWebsocketService.offline((Long)session.getAttributes().get("userId"), session);
    }

    // 连接异常回调
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }
}
