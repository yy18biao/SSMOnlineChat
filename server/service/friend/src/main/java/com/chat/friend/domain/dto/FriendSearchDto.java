package com.chat.friend.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class FriendSearchDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
}
