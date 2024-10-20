package com.chat.core.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class RabbitFriendDto {
    private String dtoType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;
    private String friendName;
    private String friendPhoto;
    private String token;
}
