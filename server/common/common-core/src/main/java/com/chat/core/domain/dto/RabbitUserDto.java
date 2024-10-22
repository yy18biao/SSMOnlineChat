package com.chat.core.domain.dto;

import lombok.Data;

@Data
public class RabbitUserDto {
    private String dtoType;
    private String token;
    private String photo;
}
