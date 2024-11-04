package com.chat.websocket.rabbit.consume;

import com.chat.core.domain.vo.RabbitMessageVo;
import com.chat.core.domain.vo.RabbitUserVo;
import com.chat.websocket.rabbit.producer.RabbitUserProducer;
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
public class RabbitUserConsume {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "user")
    public void consume(RabbitUserVo rabbitUserVo) throws IOException {
        System.out.println(rabbitUserVo);
        if(rabbitUserVo.getRespType().equals("updateHead")){
            // 修改头像成功 通知用户刷新头像
            WebSocketSession socketSession = onlineWebsocketService.getSession(rabbitUserVo.getUserId());
            if (socketSession != null) {
                socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(rabbitUserVo)));
            }
        }
    }
}
