package com.chat.core.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

// 前端发来的websocket消息实体类
@Data
public class WebSocketDto {
    private String dtoType; // 该消息的的类型 好友新增等
    private String token;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;// 如果是新增好友申请则为对方的id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chatSessionId;// 如果是新增消息的消息所属的聊天会话id
    private String messageContent; // 如果是新增消息的消息正文
    private String messageNickname;// 如果是新增消息的消息发送者的昵称
    private String messagePhoto;// 如果是新增消息的消息发送者的头像
    private Integer messageType; // 如果是新增消息的消息类型 1 文本 2 文件

    private String fileName;
}
