package com.chat.user.entity.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String userId;
    private String nickname;
    private String email;
    private String emailCode;
    private String phone;
    private String phoneCode;
    private String password;
    private String oldPassword;
    private String passwordCode;
    private String introduce;
}
