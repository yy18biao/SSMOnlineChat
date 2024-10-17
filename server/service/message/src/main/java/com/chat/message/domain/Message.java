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
@TableName("message")
public class Message extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "messageId", type = IdType.ASSIGN_ID)
    private Long messageId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;

    private String content;

    private String messageNickname;

    private String messagePhoto;

    private Integer messageType;
}