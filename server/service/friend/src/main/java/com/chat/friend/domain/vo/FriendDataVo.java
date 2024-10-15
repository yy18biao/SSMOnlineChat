package com.chat.friend.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class FriendDataVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;
    private String friendName;
    private String friendPhoto;
}
