package com.chat.user.rabbit;

import com.chat.core.domain.vo.RabbitMessageVo;
import com.chat.core.domain.vo.RabbitUserVo;
import com.chat.core.enums.ResCode;
import com.chat.security.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProducerService {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void send(RabbitUserVo rabbitUserVo) {
        try {
            rabbitTemplate.convertAndSend("ssm-chat-exchange", "ssm-chat-user", rabbitUserVo);
        } catch (Exception e) {
            log.error("生产者发送消息异常", e);
            throw new ServiceException(ResCode.FAILED_RABBIT_PRODUCE);
        }
    }
}
