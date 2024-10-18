package com.chat.websocket.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String nickname;

    private String photo;

    private String phone;

    private String code;

    private String email;

    private String introduce;

    private Integer status;
}
