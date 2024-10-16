package com.chat.friend.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class FriendDataVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long friendId;

    private String friendName;

    private String nickname;

    private String photo = "https://biao22.oss-cn-guangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.png";

    private String phone;

    private String email;

    private String introduce;
}
