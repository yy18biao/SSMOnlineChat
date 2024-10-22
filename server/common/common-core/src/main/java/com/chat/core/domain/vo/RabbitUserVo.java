package com.chat.core.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class RabbitUserVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String photo;
    private String respType;
}
