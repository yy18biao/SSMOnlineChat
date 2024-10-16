package com.chat.friend.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("friendApply")
public class FriendApply extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String nickname;
    private String photo;
}
