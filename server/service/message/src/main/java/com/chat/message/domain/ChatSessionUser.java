package com.chat.message.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("chatSessionUser")
public class ChatSessionUser extends BaseEntity {
    private Long userId;
    private Long chatSessionId;

    public ChatSessionUser(Long userId, Long chatSessionId) {
        this.userId = userId;
        this.chatSessionId = chatSessionId;
    }
}
