package com.chat.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("chatSession")
public class ChatSession extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "chatSessionId", type = IdType.ASSIGN_ID)
    private Long chatSessionId;
    private String chatSessionName;
    private String chatSessionPhoto;
    private Integer chatSessionType; // 1 单聊 2 群聊
    private String chatSessionLastMessage;
}
