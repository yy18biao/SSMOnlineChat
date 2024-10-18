package com.chat.websocket.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UpdateMessageDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    private String message;
}
