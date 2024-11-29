package com.chat.user.entity.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private String phone;
    private String password;
    private String code;
}
