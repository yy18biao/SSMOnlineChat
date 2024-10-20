package com.chat.core.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

// 响应前端前端发送的文本消息类型
@Data
public class RabbitMessageVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    private List<Long> chatSessionUserIds;
    private String respType;
    private String messageContent;
    private String messageNickname;
    private String messagePhoto;
    private String createTime;
}
