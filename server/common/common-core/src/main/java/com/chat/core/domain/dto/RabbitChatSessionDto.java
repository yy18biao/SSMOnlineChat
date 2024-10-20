package com.chat.core.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class RabbitChatSessionDto {
    private String dtoType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    private String lastMessageContent;
    private String token;
}
