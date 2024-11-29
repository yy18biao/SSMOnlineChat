package com.chat.user.entity.vo;

import lombok.Data;

@Data
public class UserVo {
    private String userId;
    private String nickname;
    private String phone;
    private String photo;
    private String email;
    private String introduce;
}
