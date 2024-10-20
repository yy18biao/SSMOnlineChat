package com.chat.websocket.rabbit.producer;

import com.chat.core.domain.dto.RabbitMessageDto;
import com.chat.core.enums.ResCode;
import com.chat.security.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void send(RabbitMessageDto rabbitMessageDto) {
        try {
            System.out.println("webP: " + rabbitMessageDto);
            rabbitTemplate.convertAndSend("ssm-chat-exchange", "ssm-chat-message", rabbitMessageDto);
        } catch (Exception e) {
            log.error("生产者发送消息异常", e);
            throw new ServiceException(ResCode.FAILED_RABBIT_PRODUCE);
        }
    }
}
