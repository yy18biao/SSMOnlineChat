package com.chat.admin.domain.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String nickname;

    private String phone;

    private String email;

    private String introduce;

    private Integer status;
}
