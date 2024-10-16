package com.chat.friend.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class SearchUserVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String photo;
    private String nickname;
}
