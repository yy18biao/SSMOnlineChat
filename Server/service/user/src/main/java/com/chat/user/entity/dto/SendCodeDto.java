package com.chat.user.entity.dto;

import lombok.Data;

@Data
public class SendCodeDto {
    private String phone;
    private Integer status;
}
