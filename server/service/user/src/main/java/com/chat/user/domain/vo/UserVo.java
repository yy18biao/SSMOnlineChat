package com.chat.user.domain.vo;

import lombok.Data;

@Data
public class UserVo {
    private String nickname;

    private String photo;

    private String phone;

    private String code;

    private String email;

    private String introduce;

    private Integer status;
}
