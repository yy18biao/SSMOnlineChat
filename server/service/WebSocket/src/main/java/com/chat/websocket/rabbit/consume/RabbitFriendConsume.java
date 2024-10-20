package com.chat.websocket.rabbit.consume;

import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.vo.RabbitFriendApplyVo;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.websocket.service.OnlineWebsocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@Slf4j
public class RabbitFriendConsume {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "friend")
    public void consume(RabbitFriendApplyVo rabbitFriendApplyVo) throws IOException {
        if(rabbitFriendApplyVo.getRespType().equals("addFriendApply")){
            // 新增好友申请
            // 如果对方在线则通知对方
            WebSocketSession socketSession =onlineWebsocketService.getSession(rabbitFriendApplyVo.getFriendId());
            if(socketSession != null) {
                socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(rabbitFriendApplyVo)));
            }
        }
    }
}
