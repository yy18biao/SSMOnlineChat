package com.chat.websocket.rabbit.consume;

import com.chat.core.domain.vo.RabbitMessageVo;
import com.chat.websocket.service.OnlineWebsocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class RabbitMessageConsume {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "message")
    public void consume(RabbitMessageVo rabbitMessageVo) throws IOException {
        if(rabbitMessageVo.getRespType().equals("addTextMessage")){
           // 转发给在这个会话中所有在线的用户
            List<Long> userIds = rabbitMessageVo.getChatSessionUserIds();
            for (Long userId : userIds) {
                WebSocketSession socketSession = onlineWebsocketService.getSession(userId);
                if (socketSession != null) {
                    socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(rabbitMessageVo)));
                }
            }
        }
    }
}
