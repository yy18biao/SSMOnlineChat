package com.chat.chatSession.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ChatSessionVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;
    private String chatSessionName;
    private String chatSessionPhoto;
    private String chatSessionLastMessage;
    private Integer chatSessionType; // 1 单聊 2 群聊
}
