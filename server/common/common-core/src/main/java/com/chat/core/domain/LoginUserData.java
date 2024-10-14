package com.chat.core.domain;

import lombok.Data;

@Data
public class LoginUserData {
    private Integer identity; // 身份信息 1 普通用户 2 管理员
    private String nickname;
    private String photo;
}
