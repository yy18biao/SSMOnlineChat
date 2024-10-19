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

    // 1 文本消息 2 文件消息 3 用户上线token
    private Integer messageType;
}
