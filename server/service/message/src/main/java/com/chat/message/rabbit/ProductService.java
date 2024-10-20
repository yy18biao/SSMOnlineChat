package com.chat.message.rabbit;

import com.chat.core.domain.dto.RabbitMessageDto;
import com.chat.core.domain.vo.RabbitMessageVo;
import com.chat.core.enums.ResCode;
import com.chat.security.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductService {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void send(RabbitMessageVo rabbitMessageVo) {
        try {
            rabbitTemplate.convertAndSend("ssm-chat-exchange", "ssm-chat-message", rabbitMessageVo);
        } catch (Exception e) {
            log.error("生产者发送消息异常", e);
            throw new ServiceException(ResCode.FAILED_RABBIT_PRODUCE);
        }
    }
}
