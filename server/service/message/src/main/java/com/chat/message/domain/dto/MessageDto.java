package com.chat.message.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class MessageDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;

    private String token;

    private String content;

    private String messageNickname;

    private String messagePhoto;

    private Integer messageType;
}
