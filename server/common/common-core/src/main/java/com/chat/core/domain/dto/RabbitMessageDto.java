package com.chat.core.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

// 发送到rabbitmq中让消息处理模块接收的消息
@Data
public class RabbitMessageDto {
    private String dtoType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    private String token;
    private String content;
    private String messageNickname;
    private String messagePhoto;
    private Integer messageType;
}
