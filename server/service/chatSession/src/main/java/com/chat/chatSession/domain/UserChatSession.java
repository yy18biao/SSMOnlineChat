package com.chat.chatSession.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("userChatSession")
public class UserChatSession extends BaseEntity {
    private Long userId;
    private Long chatSessionId;
}
