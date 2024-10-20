package com.chat.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user")
public class User extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "userId", type = IdType.ASSIGN_ID)
    private Long userId;

    private String password;

    private String nickname;

    private String photo = "https://biao22.oss-cn-guangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.png";

    private String phone;

    private String code;

    private String email;

    private String introduce;

    private Integer status;
}
