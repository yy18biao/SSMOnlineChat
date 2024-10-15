package com.chat.friend.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("friend")
public class Friend extends BaseEntity {
    private Long userId;
    private Long friendId;
    private String friendName;
    private String friendPhoto = "https://biao22.oss-cn-guangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.png";
}
