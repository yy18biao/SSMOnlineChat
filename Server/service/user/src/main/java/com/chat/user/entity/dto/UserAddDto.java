package com.chat.user.entity.dto;

import lombok.Data;

@Data
public class UserAddDto {
    private String phone;
    private String nickname;
    private String password;
    private String code;
}
