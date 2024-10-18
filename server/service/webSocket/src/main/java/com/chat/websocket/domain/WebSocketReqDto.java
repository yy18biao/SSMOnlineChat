package com.chat.websocket.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class WebSocketReqDto {
    private String token;
    private Integer messageType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    private String content;
    private String messageNickname;
    private String messagePhoto;
}
