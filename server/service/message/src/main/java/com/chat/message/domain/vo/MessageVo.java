package com.chat.message.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class MessageVo{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;

    private String content;

    private String nickname;

    private Integer messageType;

    private String photo;

    private String createTime;
}