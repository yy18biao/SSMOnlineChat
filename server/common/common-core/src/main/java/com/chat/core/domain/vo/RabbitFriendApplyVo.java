package com.chat.core.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class RabbitFriendApplyVo {
    private String msg;
    private String respType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;

    public RabbitFriendApplyVo() {
    }

    public RabbitFriendApplyVo(String msg, String respType) {
        this.msg = msg;
        this.respType = respType;
    }
}
