package com.chat.admin.domain.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ApplyVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String nickName;

    private String phone;

    private String email;

    private String introduce;
}
